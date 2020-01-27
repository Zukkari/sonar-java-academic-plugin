package io.github.zukkari.checks

import cats.implicits._
import cats.kernel.Monoid
import io.github.zukkari.base.{Formatter, SensorRule}
import io.github.zukkari.config.{ConfigurationProperties, ConfigurationProperty}
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree, Tree}
import org.sonar.plugins.java.api.tree.Tree.Kind
import io.github.zukkari.syntax.SymbolSyntax._
import org.sonar.api.config.Configuration

import scala.math.pow

@Rule(key = "StableAbstractionBreaker")
class StableAbstractionBreaker
    extends JavaCheck
    with SensorRule
    with Formatter {

  var unstableDependencies: UnstableDependencies = _

  private var classToAbstractionMap: Map[String, Double] = Map.empty

  private var allowedDistance: Double = _

  override def configure(configuration: Configuration): Unit = {
    allowedDistance = configuration
      .getDouble(
        ConfigurationProperties.STABLE_ABSTRACTION_BREAKER_ALLOWED_DISTANCE_FROM_MAIN.key)
      .orElse(
        ConfigurationProperties.STABLE_ABSTRACTION_BREAKER_ALLOWED_DISTANCE_FROM_MAIN.defaultValue.toDouble)
  }

  override def scan(t: Tree): Unit = {
    val visitor = new StableAbstractionBreakerVisitor
    visitor.visit(t)

    classToAbstractionMap ++= visitor.classToAbstractionMap
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    val declarations = unstableDependencies.declarationMap

    classToAbstractionMap.foreachEntry {
      case (className, abstraction) =>
        for {
          instability <- unstableDependencies.classToInstabilityMap.get(
            className)
          distance = 1.0 - pow(abstraction - instability, 2)
          if distance < -allowedDistance || distance > allowedDistance
          declaration <- declarations.get(className)
        } yield {
          report(
            sensorContext,
            s"Stable abstraction breaker: distance from main is ${format(
              distance)} which is greater than ${format(allowedDistance)} configured",
            declaration,
            "StableAbstractionBreaker"
          )
        }
    }
  }
}

class StableAbstractionBreakerVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS)

  var classToAbstractionMap: Map[String, Double] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]
    val symbolName =
      classTree.symbol.fullyQualifiedName.getOrElse(Monoid[String].empty)

    val visitor = new AbstractMethodVisitor
    visitor.visit(classTree)

    classToAbstractionMap += symbolName -> visitor.abstraction

    super.visitNode(tree)
  }
}

class AbstractMethodVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] = List(Kind.METHOD)

  private var abstractMethods: Int = Monoid[Int].empty
  private var concreteMethods: Int = Monoid[Int].empty

  def abstraction: Double =
    abstractMethods.toDouble / concreteMethods.max(1).toDouble

  override def visitNode(tree: Tree): Unit = {
    val methodTree = tree.asInstanceOf[MethodTree]

    val (newAbstract, newConcrete) =
      if (methodTree.symbol.isAbstract)
        (abstractMethods + 1, concreteMethods)
      else (abstractMethods, concreteMethods + 1)

    abstractMethods = newAbstract
    concreteMethods = newConcrete

    super.visitNode(tree)
  }
}
