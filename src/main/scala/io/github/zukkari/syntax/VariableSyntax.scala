package io.github.zukkari.syntax

import org.sonar.plugins.java.api.tree.VariableTree
import io.github.zukkari.syntax.SymbolSyntax._

object VariableSyntax {
  implicit class VarOps(variableTree: VariableTree) {
    def variableType: Option[String] =
      Option(variableTree.`type`())
        .map(_.symbolType())
        .map(_.symbol())
        .flatMap(_.fullyQualifiedName)
  }
}
