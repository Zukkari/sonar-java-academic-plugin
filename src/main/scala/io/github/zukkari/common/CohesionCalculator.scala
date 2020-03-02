package io.github.zukkari.common

import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}
import io.github.zukkari.syntax.ClassSyntax._

class CohesionCalculator {
  def calculate(tree: ClassTree): Int = {
    val variables = tree.variables.toList
    val variableNames = variables.map(_.symbol.name)

    val methods = tree.methods.toList

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

    cohesion
  }

  private def methodToVariables(method: MethodTree): Set[String] =
    new VariableUsageLocator().variables(method)
}
