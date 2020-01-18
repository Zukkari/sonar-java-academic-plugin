package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.common.VariableUsageLocator
import io.github.zukkari.syntax.ClassSyntax._
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}

@Rule(key = "BlobClass")
class BlobClass extends JavaRule {
  private var context: JavaFileScannerContext = _

  private val numberOfVariables = 13
  private val numberOfMethods = 22

  private val lackOfCohesion = 40

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
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
    val variableNames = variables.map(_.symbol.name)

    val methodVariables = methods
      .map(methodToVariables)
      .map(_.filter(variableNames contains _))

    val cohesion = methodVariables
      .combinations(2)
      .map {
        case x :: y :: _ => if (x.intersect(y).isEmpty) -1 else 1
        case _           => 0
      }
      .sum

    report(s"Blob class: cohesion is below threshold: $lackOfCohesion",
           tree,
           cohesion <= lackOfCohesion)

    super.visitClass(tree)
  }

  private def methodToVariables(method: MethodTree): Set[String] =
    new VariableUsageLocator().variables(method)
}
