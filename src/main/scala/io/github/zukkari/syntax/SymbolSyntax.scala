package io.github.zukkari.syntax

object SymbolSyntax {
  implicit class SymbolOps(symbol: org.sonar.plugins.java.api.semantic.Symbol) {
    def ownerAndSymbolName: Option[String] =
      Option(symbol.owner)
        .map(_.name)
        .map(owner => s"$owner#${symbol.name}")
  }
}
