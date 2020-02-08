package io.github.zukkari.checks

import java.util.UUID

import io.github.zukkari.base.SensorRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Configuration
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree, Tree}
import org.sonar.plugins.java.api.tree.Tree.Kind
import io.github.zukkari.syntax.SymbolSyntax._

import scala.jdk.CollectionConverters._

case class Argument(argType: String)

case class ClassMethod(name: String, args: List[Argument])

@Rule(key = "AlternativeClassesWithDifferentInterfaces")
class AlternativeClassesWithDifferentInterfaces
    extends JavaCheck
    with SensorRule {

  private var minParameterCount: Int = _
  private var minNumberOfCommonMethods: Int = _

  private var declarationMap: Map[String, Declaration] = Map.empty
  private var classToMethodMap: Map[String, List[ClassMethod]] = Map.empty

  override def configure(configuration: Configuration): Unit = {
    minParameterCount = configuration
      .getInt(ConfigurationProperties.ALTERNATIVE_CLASSES_MIN_PARAM_COUNT.key)
      .orElse(2)

    minNumberOfCommonMethods = configuration
      .getInt(
        ConfigurationProperties.ALTERNATIVE_CLASSES_MIN_COMMON_METHODS.key)
      .orElse(2)
  }

  override def scan(t: Tree): Unit = {
    val visitor = new AlternativeClassVisitor(inputFile)
    visitor.visit(t)

    declarationMap ++= visitor.declarationMap
    classToMethodMap ++= visitor.classToMethodMap
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    declarationMap.keySet.toList
      .combinations(2)
      .map {
        case first :: second :: _ => Option((first, second))
        case _                    => None
      }
      .filter(_.nonEmpty)
      .map(_.get)
      .foreach {
        case (first, second) =>
          for {
            firstMethods <- classToMethodMap.get(first)
            secondMethods <- classToMethodMap.get(second)
            if commonMethodCount(firstMethods, secondMethods) >= minNumberOfCommonMethods

            firstDeclaration <- declarationMap.get(first)
            secondDeclaration <- declarationMap.get(second)
          } yield {
            report(
              sensorContext,
              s"Alternative classes with different classes: similar class '$second'",
              firstDeclaration,
              "AlternativeClassesWithDifferentInterfaces"
            )

            report(
              sensorContext,
              s"Alternative classes with different classes: similar class '$first'",
              secondDeclaration,
              "AlternativeClassesWithDifferentInterfaces"
            )
          }
      }
  }

  private def commonMethodCount(firstMethods: List[ClassMethod],
                                secondMethods: List[ClassMethod]): Int = {
    firstMethods
      .filter(_.args.length >= minParameterCount)
      .count(first => secondMethods.exists(second => second.args == first.args))
  }
}

class AlternativeClassVisitor(val inputFile: InputFile)
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS)

  var declarationMap: Map[String, Declaration] = Map.empty
  var classToMethodMap: Map[String, List[ClassMethod]] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]

    val className = classTree.symbol.fullyQualifiedName
      .getOrElse(UUID.randomUUID.toString)
    declarationMap += className -> Declaration(inputFile,
                                               classTree.firstToken.line)

    val visitor = new MethodParameterVisitor
    visitor.visit(classTree)

    classToMethodMap += className -> visitor.methods

    super.visitNode(tree)
  }
}

class MethodParameterVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] = List(Kind.METHOD)

  var methods: List[ClassMethod] = Nil

  override def visitNode(tree: Tree): Unit = {
    val methodTree = tree.asInstanceOf[MethodTree]

    val methodName: String =
      Option(methodTree.simpleName).map(_.toString).getOrElse("")

    val parameters: List[Argument] = methodTree.parameters.asScala
      .map(_.symbol)
      .map(_.`type`)
      .map(_.toString)
      .map(Argument)
      .toList

    methods ::= ClassMethod(methodName, parameters)

    super.visitNode(tree)
  }
}
