package io.github.zukkari.checks

import io.github.zukkari.base.SensorRule
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.check.Rule
import org.sonar.java.ast.visitors.{
  CognitiveComplexityVisitor,
  LinesOfCodeVisitor
}
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree._

import scala.collection.mutable
import scala.jdk.CollectionConverters._

@Rule(key = "BrainMethod")
class BrainMethod(
    val highNumberOfLinesOfCode: Int,
    val highCyclomaticComplexity: Int,
    val maxNestingDepth: Int,
    val manyAccessedVariables: Int
) extends JavaCheck
    with SensorRule {

  def this() = this(130, 31, 3, 7)

  var complexityMap: Map[MethodTree, Int] = Map.empty
  var nestingMap: Map[MethodTree, Int] = Map.empty

  var classLinesOfCodeMap: Map[ClassTree, Int] = Map.empty
  var classVariableMap: Map[ClassTree, Set[String]] = Map.empty

  var declarationMap: Map[MethodTree, Declaration] = Map.empty

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
    extends SonarAcademicSubscriptionVisitor {
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

        classVariables += classTree -> classTree.members.asScala
          .filter(_.is(Kind.VARIABLE))
          .map(_.asInstanceOf[VariableTree])
          .map(_.simpleName.toString)
          .toSet
    }

    super.visitNode(tree)
  }

  private def complexity(method: MethodTree): Int =
    CognitiveComplexityVisitor.methodComplexity(method).complexity
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
