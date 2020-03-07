package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.common.{CohesionCalculator, VariableUsageLocator}
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.ClassSyntax._
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}

@Rule(key = "BlobClass")
class BlobClass extends JavaRule {
  private var context: JavaFileScannerContext = _

  private var numberOfVariables: Double = _
  private var numberOfMethods: Double = _

  private var lackOfCohesion: Double = _

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    numberOfVariables = config
      .flatMap(
        _.getDouble(ConfigurationProperties.BLOB_CLASS_NUM_OF_VARIABLES.key))
      .orElse(
        ConfigurationProperties.BLOB_CLASS_NUM_OF_VARIABLES.defaultValue.toDouble)

    numberOfMethods = config
      .flatMap(
        _.getDouble(ConfigurationProperties.BLOB_CLASS_NUM_OF_METHODS.key))
      .orElse(
        ConfigurationProperties.BLOB_CLASS_NUM_OF_METHODS.defaultValue.toDouble)

    lackOfCohesion = config
      .flatMap(
        _.getDouble(ConfigurationProperties.BLOB_CLASS_LACK_OF_COHESION.key))
      .orElse(
        ConfigurationProperties.BLOB_CLASS_LACK_OF_COHESION.defaultValue.toDouble)

    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitClass(tree: ClassTree): Unit = {
    // Find variables of the class
    val variables = tree.variables.toList

    if (variables.size < numberOfVariables) {
      // Number of variables lower than threshold
      // so no point in going further
      return
    }

    // Find methods of the class
    val methods = tree.methods.toList

    if (methods.size < numberOfMethods) {
      // Number of methods is lower than threshold
      // no point in going further
      return
    }

    // Calculate cohesion between variables
    val cohesion = new CohesionCalculator().calculate(tree)

    report(s"Blob class: cohesion is below threshold: $lackOfCohesion",
           tree,
           cohesion <= lackOfCohesion)

    super.visitClass(tree)
  }

  private def methodToVariables(method: MethodTree): Set[String] =
    new VariableUsageLocator().variables(method)
}
