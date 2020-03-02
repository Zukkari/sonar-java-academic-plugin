package io.github.zukkari.checks

import io.github.zukkari.base.SensorRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Configuration
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{
  ClassTree,
  MemberSelectExpressionTree,
  MethodInvocationTree,
  Tree
}
import io.github.zukkari.syntax.SymbolSyntax._
@Rule(key = "InappropriateIntimacy")
class InappropriateIntimacy extends JavaCheck with SensorRule {
  type InvocationMap = Map[String, Int]

  private var maxNumberOfCalls: Int = _

  private var declarationMap: Map[String, Declaration] = Map.empty
  private var classToInvocationMap: Map[String, InvocationMap] = Map.empty

  override def configure(configuration: Configuration): Unit = {
    maxNumberOfCalls = configuration
      .getInt(
        ConfigurationProperties.INAPPROPRIATE_INTIMACY_NUMBER_OF_CALLS.key)
      .orElse(4)
  }

  override def scan(t: Tree): Unit = {
    val visitor = new InappropriateIntimacyVisitor(inputFile)
    visitor.visit(t)

    declarationMap ++= visitor.declarationMap
    classToInvocationMap ++= visitor.classInvocationMap
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    declarationMap.keySet.toList
      .combinations(2)
      .map {
        case first :: second :: _ =>
          Some((first, second))
        case _ =>
          None
      }
      .filter(_.nonEmpty)
      .map(_.get)
      .foreach {
        case (first, second) =>
          for {
            firstInvocations <- classToInvocationMap.get(first)
            secondInvocations <- classToInvocationMap.get(second)

            firstInvokesSecond <- firstInvocations.get(second)
            secondInvokesFirst <- secondInvocations.get(first)
            if firstInvokesSecond + secondInvokesFirst > maxNumberOfCalls

            firstDeclaration <- declarationMap.get(first)
            secondDeclaration <- declarationMap.get(second)
          } yield {
            report(
              sensorContext,
              s"Inappropriate intimacy: number of method calls ${firstInvokesSecond + secondInvokesFirst} with class $second is greater than configured $maxNumberOfCalls",
              firstDeclaration,
              "InappropriateIntimacy"
            )

            report(
              sensorContext,
              s"Inappropriate intimacy: number of method calls ${firstInvokesSecond + secondInvokesFirst} with class $first is greater than configured $maxNumberOfCalls",
              secondDeclaration,
              "InappropriateIntimacy"
            )
          }
      }
  }
}

class InappropriateIntimacyVisitor(inputFile: InputFile)
    extends SonarAcademicSubscriptionVisitor {
  type InvocationMap = Map[String, Int]

  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS)

  var declarationMap: Map[String, Declaration] = Map.empty
  var classInvocationMap: Map[String, InvocationMap] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]

    declarationMap += classTree
      .symbol()
      .fullyQualifiedName
      .getOrElse("Anonymous") -> Declaration(inputFile,
                                             classTree.firstToken.line)

    val visitor = new MethodInvocationVisitor
    visitor.visit(tree)

    classInvocationMap += classTree.symbol.fullyQualifiedName
      .getOrElse("Anonymous") -> visitor.classToInvocationCount

    super.visitNode(tree)
  }
}

class MethodInvocationVisitor extends SonarAcademicSubscriptionVisitor {
  type InvocationMap = Map[String, Int]

  override def nodesToVisit: List[Kind] = List(Kind.METHOD_INVOCATION)

  var classToInvocationCount: InvocationMap = Map.empty

  override def visitNode(tree: Tree): Unit = {
    Option(tree.asInstanceOf[MethodInvocationTree])
      .map(_.methodSelect)
      .filter(_.is(Kind.MEMBER_SELECT))
      .map(_.asInstanceOf[MemberSelectExpressionTree])
      .map(_.expression)
      .map(_.symbolType)
      .filter(_ != null)
      .map(_.symbol)
      .flatMap(_.fullyQualifiedName) match {
      case Some(ident) =>
        classToInvocationCount += ident -> (classToInvocationCount.getOrElse(
          ident,
          0) + 1)
      case _ =>
    }

    super.visitNode(tree)
  }
}
