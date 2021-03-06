package io.github.zukkari.checks

import java.util.UUID

import cats.implicits._
import io.github.zukkari.base.{Formatter, SensorRule}
import io.github.zukkari.syntax.SymbolSyntax._
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{ClassTree, Tree, VariableTree}

import scala.jdk.CollectionConverters._

@Rule(key = "UnstableDependencies")
class UnstableDependencies extends JavaCheck with SensorRule with Formatter {
  var declarationMap: Map[String, Declaration] = Map.empty
  var classToInstabilityMap: Map[String, Double] = Map.empty

  private var classToDependenciesMap: Map[String, Set[String]] = Map.empty

  override def scan(t: Tree): Unit = {
    val visitor = new UnstableDependenciesClassVisitor(inputFile)
    visitor.visit(t)

    declarationMap ++= visitor.declarationMap
    classToDependenciesMap ++= visitor.dependencies
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    val declaredClasses = declarationMap.keySet
    classToDependenciesMap = classToDependenciesMap.map {
      // Keep only dependencies declared in this project
      case (className, dependencies) =>
        (className, dependencies.intersect(declaredClasses))
    }

    classToInstabilityMap = classToDependenciesMap.map {
      case (className, dependencies) =>
        val afferentCoupling =
          classToDependenciesMap.values.count(_.contains(className)).toDouble
        val efferentCoupling = dependencies.size.toDouble
        val instability = efferentCoupling / (efferentCoupling + afferentCoupling)
          .max(1.0)
        (className, instability)
    }

    classToDependenciesMap.foreachEntry {
      case (className, dependencies) =>
        for {
          dependencyInstability <- dependencies
            .map(dep => (dep, classToInstabilityMap.get(dep)))
            .toList
            .traverse {
              case (dep, maybeStability) =>
                maybeStability match {
                  case Some(stability) => (dep, stability).some
                  case _               => none
                }
            }
          classOwnInstability <- classToInstabilityMap.get(className)
          if dependencyInstability.exists {
            case (_, inStability) => classOwnInstability < inStability
          }
          declaration <- declarationMap.get(className)
        } yield {
          val unstable = dependencyInstability.filter {
            case (_, instability) => classOwnInstability < instability
          }

          report(
            sensorContext,
            s"Unstable dependencies: the following dependencies are less stable than the class '$className (instability ${format(classOwnInstability)})': ${unstable
              .map {
                case (name, stability) =>
                  name + s" (instability ${format(stability)})"
              }
              .mkString(", ")}",
            declaration,
            "UnstableDependencies"
          )
        }
    }
  }
}

class UnstableDependenciesClassVisitor(val inputFile: InputFile)
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS)

  var declarationMap: Map[String, Declaration] = Map.empty
  var dependencies: Map[String, Set[String]] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]
    val symbolName =
      classTree.symbol.fullyQualifiedName.getOrElse(UUID.randomUUID.toString)

    declarationMap += symbolName -> Declaration(inputFile, tree.firstToken.line)

    dependencies += symbolName -> classTree.members.asScala
      .filter(_.is(Kind.VARIABLE))
      .map(_.asInstanceOf[VariableTree])
      .map(_.symbol)
      .map(_.`type`)
      .map(_.symbol.fullyQualifiedName)
      .filter(_.nonEmpty)
      .map(_.get)
      .toSet

    super.visitNode(tree)
  }
}
