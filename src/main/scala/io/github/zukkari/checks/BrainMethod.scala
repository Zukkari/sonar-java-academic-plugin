package io.github.zukkari.checks

import io.github.zukkari.base.{ComplexityAccessor, SensorRule}
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.sonar.java.ast.visitors.LinesOfCodeVisitor
import io.github.zukkari.syntax.ClassSyntax._
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Configuration
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree._

import scala.collection.mutable

@Rule(key = "BrainMethod")
class BrainMethod extends JavaCheck with SensorRule {

  private var highNumberOfLinesOfCode: Int = _
  private var highCyclomaticComplexity: Int = _
  private var maxNestingDepth: Int = _
  private var manyAccessedVariables: Int = _

  var complexityMap: Map[MethodTree, Int] = Map.empty
  var nestingMap: Map[MethodTree, Int] = Map.empty

  var classLinesOfCodeMap: Map[ClassTree, Int] = Map.empty
  var classVariableMap: Map[ClassTree, Set[String]] = Map.empty

  var declarationMap: Map[MethodTree, Declaration] = Map.empty

  override def configure(configuration: Configuration): Unit = {
    highNumberOfLinesOfCode = configuration
      .getInt(ConfigurationProperties.BRAIN_METHOD_HIGH_NUMBER_OF_LOC.key)
      .orElse(130)

    highCyclomaticComplexity = configuration
      .getInt(
        ConfigurationProperties.BRAIN_METHOD_HIGH_CYCLOMATIC_COMPLEXITY.key)
      .orElse(31)

    maxNestingDepth = configuration
      .getInt(ConfigurationProperties.BRAIN_METHOD_HIGH_NESTING_DEPTH.key)
      .orElse(3)

    manyAccessedVariables = configuration
      .getInt(ConfigurationProperties.BRAIN_METHOD_MANY_ACCESSED_VARIABLES.key)
      .orElse(7)
  }

  override def scan(t: Tree): Unit = {
    val visitor = new BrainMethodVisitor(inputFile)
    visitor.visit(t)

    complexityMap ++= visitor.complexityMap
    nestingMap ++= visitor.nestingMap

    classLinesOfCodeMap ++= visitor.classLinesOfCodeMap
    classVariableMap ++= visitor.classVariables

    declarationMap ++= visitor.declarationMap
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    complexityMap.keySet.foreach { method =>
      for {
        complexity <- complexityMap.get(method)
        if complexity >= highCyclomaticComplexity
        nesting <- nestingMap.get(method)
        if nesting >= maxNestingDepth
        parentClassLoc <- classLinesOfCodeMap.get(
          method.parent.asInstanceOf[ClassTree])
        if parentClassLoc >= highNumberOfLinesOfCode
        variables <- classVariableMap.get(method.parent.asInstanceOf[ClassTree])
        if {
          val visitor = new IdentifierVisitor
          visitor.visit(method)
          visitor.identifiers.intersect(variables).size >= manyAccessedVariables
        }
        declaration <- declarationMap.get(method)
      } yield {
        report(
          sensorContext,
          "Brain method",
          declaration,
          "BrainMethod"
        )
      }
    }
  }
}

class BrainMethodVisitor(inputFile: InputFile)
    extends SonarAcademicSubscriptionVisitor
    with ComplexityAccessor {
  var complexityMap: Map[MethodTree, Int] = Map.empty
  var nestingMap: Map[MethodTree, Int] = Map.empty

  var classLinesOfCodeMap: Map[ClassTree, Int] = Map.empty
  var classVariables: Map[ClassTree, Set[String]] = Map.empty

  var declarationMap: Map[MethodTree, Declaration] = Map.empty

  override def nodesToVisit: List[Tree.Kind] = List(Kind.METHOD, Kind.CLASS)

  override def visitNode(tree: Tree): Unit = {
    tree match {
      case method: MethodTree =>
        declarationMap += method -> Declaration(inputFile,
                                                method.firstToken.line)
        complexityMap += method -> complexity(method)

        val visitor = new NestingVisitor
        visitor.visit(method)

        nestingMap += method -> visitor.maxNestingLevel
      case classTree: ClassTree =>
        val visitor = new LinesOfCodeVisitor()

        classLinesOfCodeMap += classTree -> visitor.linesOfCode(classTree)

        classVariables += classTree -> classTree.variables
          .map(_.simpleName.toString)
          .toSet
    }

    super.visitNode(tree)
  }
}

class NestingVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] =
    List(Kind.IF_STATEMENT,
         Kind.FOR_STATEMENT,
         Kind.WHILE_STATEMENT,
         Kind.SWITCH_EXPRESSION,
         Kind.TRY_STATEMENT)

  private val nestingLevel: mutable.Queue[Tree] = mutable.Queue.empty
  var maxNestingLevel = 0

  override def visitNode(tree: Tree): Unit = {
    maxNestingLevel =
      if (nestingLevel.size + 1 > maxNestingLevel) nestingLevel.size + 1
      else maxNestingLevel

    tree match {
      case ifStatementTree: IfStatementTree =>
        nestingLevel.enqueue(ifStatementTree.ifKeyword)
        visit(ifStatementTree.thenStatement)
        nestingLevel.dequeue()

      case forStatementTree: ForStatementTree =>
        nestingLevel.enqueue(forStatementTree.statement)
        visit(forStatementTree.statement)
        nestingLevel.dequeue()

      case whileStatementTree: WhileStatementTree =>
        nestingLevel.enqueue(whileStatementTree.statement)
        visit(whileStatementTree.statement)
        nestingLevel.dequeue()

      case switchStatementTree: SwitchStatementTree =>
        nestingLevel.enqueue(switchStatementTree)
        visit(switchStatementTree)
        nestingLevel.dequeue()

      case tryStatementTree: TryStatementTree =>
        nestingLevel.enqueue(tryStatementTree.block)
        visit(tryStatementTree.block)
        nestingLevel.dequeue()
        visit(tryStatementTree.resourceList())
        tryStatementTree.catches.forEach(visit)
        visit(tryStatementTree.finallyBlock())
    }
  }
}

class IdentifierVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] = List(Kind.IDENTIFIER)

  var identifiers: Set[String] = Set.empty

  override def visitNode(tree: Tree): Unit = {
    identifiers += tree.toString

    super.visitNode(tree)
  }
}
