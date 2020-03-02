package io.github.zukkari.checks

import java.util.UUID

import cats.implicits._
import io.github.zukkari.base.{ComplexityAccessor, JavaRule}
import io.github.zukkari.common.VariableUsageLocator
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.ClassSyntax._
import io.github.zukkari.syntax.SymbolSyntax._
import io.github.zukkari.util.Log
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}
@Rule(key = "GodClass")
class GodClass extends JavaRule with ComplexityAccessor {

  private var accessToForeignData: Int = _
  private var tightClassCohesion: Double = _
  private var classComplexity: Int = _

  private val log = Log(classOf[GodClass])

  private var context: JavaFileScannerContext = _

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    accessToForeignData = config
      .flatMap(
        _.getInt(ConfigurationProperties.GOD_CLASS_ACCESS_TO_FOREIGN_DATA.key))
      .orElse(
        ConfigurationProperties.GOD_CLASS_ACCESS_TO_FOREIGN_DATA.defaultValue.toInt)

    tightClassCohesion = config
      .flatMap(
        _.getDouble(ConfigurationProperties.GOD_CLASS_TIGHT_COHESION.key))
      .orElse(
        ConfigurationProperties.GOD_CLASS_TIGHT_COHESION.defaultValue.toDouble)

    classComplexity = config
      .flatMap(_.getInt(ConfigurationProperties.GOD_CLASS_CLASS_COMPLEXITY.key))
      .orElse(
        ConfigurationProperties.GOD_CLASS_CLASS_COMPLEXITY.defaultValue.toInt)

    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitClass(tree: ClassTree): Unit = {
    val owner =
      tree.symbol.fullyQualifiedName.getOrElse(UUID.randomUUID.toString)
    val visitor = new ForeignVariableUsageLocator(owner, Set())
    visitor.visit(tree)
    val accessToForeignData = visitor.foreignVariableUsage
    log.info(
      s"Access to foreign data: $accessToForeignData and threshold is ${this.accessToForeignData}")

    val methods = tree.methods
    val weightedMethodComplexity = complexity(methods)
    log.info(
      s"Weighted method complexity: $weightedMethodComplexity while threshold is ${this.classComplexity}")

    val methodCohesion = cohesion(methods)
    log.info(
      s"Method cohesion is $methodCohesion while threshold is ${this.tightClassCohesion}")

    report(
      "God class: access to foreign data too high and class cohesion is low and method complexity is low",
      tree,
      accessToForeignData > this.accessToForeignData &&
        weightedMethodComplexity >= this.classComplexity &&
        methodCohesion < this.tightClassCohesion
    )

    super.visitClass(tree)
  }

  private def methodVariables(methodTree: MethodTree): Set[String] =
    new VariableUsageLocator().variables(methodTree)

  private def cohesion(methods: Iterable[MethodTree]): Double =
    methods
      .map(methodVariables)
      .toList
      .combinations(2)
      .map {
        case first :: second :: _ => (first, second).some
        case _                    => None
      }
      .filter(_.nonEmpty)
      .map(_.get)
      .count {
        case (first, second) => first.intersect(second).size > 1
      } / methods.size.max(1).toDouble
}
