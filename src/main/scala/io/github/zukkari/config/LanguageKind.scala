package io.github.zukkari.config

sealed trait LanguageKind {
  val key: String
}

case object Java extends LanguageKind {
  override val key: String = "java"
}
