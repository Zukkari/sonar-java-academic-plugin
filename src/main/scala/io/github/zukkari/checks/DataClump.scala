package io.github.zukkari.checks

import cats.effect.IO
import io.github.zukkari.base.SensorRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.ClassSyntax._
import io.github.zukkari.util.Log
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Configuration
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{ClassTree, PrimitiveTypeTree, Tree}
import io.github.zukkari.syntax.SymbolSyntax._

case class Declaration(f: InputFile, line: Int)

case class Variable(name: String, declaringClass: String)

object DataClump {
  val key = "DataClump"
}

@Rule(key = "DataClump")
class DataClump extends JavaCheck with SensorRule {
  private val log = Log(classOf[DataClump])

  private var commonVariableThreshold: Int = _

  private var classMap: Map[String, Set[Variable]] = Map.empty
  private var declarationMap: Map[String, Declaration] = Map.empty

  override def configure(configuration: Configuration): Unit = {
    commonVariableThreshold = configuration
      .getInt(ConfigurationProperties.DATA_CLUMP_COMMON_VARIABLES.key)
      .orElse(3)
  }

  override def scan(t: Tree): Unit = {
    val visitor = new DataClumpClassVisitor(inputFile)
    visitor.scanTree(t)

    classMap ++= visitor.classVariableMap
    declarationMap ++= visitor.declarationMap
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    classMap.keySet.toList
      .combinations(2)
      .foreach {
        case first :: second :: _ =>
          for {
            firstVariables <- classMap.get(first)
            secondVariables <- classMap.get(second)
            if firstVariables
              .intersect(secondVariables)
              .size >= commonVariableThreshold
            firstDeclaration <- declarationMap.get(first)
            secondDeclaration <- declarationMap.get(second)
          } yield
            IO {
              report(
                sensorContext,
                s"Data clump: similar to class: '$second'",
                firstDeclaration,
                DataClump.key
              )

              report(
                sensorContext,
                s"Data clump: similar to class: '$first'",
                secondDeclaration,
                DataClump.key
              )

              log.info(
                s"Class '$first' and '$second' have following variables in common: ${firstVariables
                  .intersect(secondVariables)}")
            }.unsafeRunSync()
        case _ =>
      }
  }
}

class DataClumpClassVisitor(val f: InputFile)
    extends SonarAcademicSubscriptionVisitor {
  private val log = Log(classOf[DataClumpClassVisitor])

  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS)

  var declarationMap: Map[String, Declaration] = Map.empty
  var classVariableMap: Map[String, Set[Variable]] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]

    classTree.symbol().fullyQualifiedName match {
      case None =>
        log.info(s"Failed to determine class name for $tree")
        super.visitNode(tree)
      case Some(className) =>
        declarationMap += className -> Declaration(f, classTree.firstToken.line)

        val variables = classTree.variables
          .map(variable =>
            variable.`type`() match {
              case primitive: PrimitiveTypeTree =>
                Variable(primitive.keyword.text, variable.simpleName.name)
              case _ =>
                Variable(variable.`type`.toString, variable.simpleName.name)
          })
          .toSet

        classVariableMap += className -> variables

        super.visitNode(tree)
    }
  }
}
