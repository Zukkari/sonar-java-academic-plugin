package io.github.zukkari.config

import org.scalatest.flatspec.AnyFlatSpec

class LanguageSpec extends AnyFlatSpec {
  "Language" should "contain Java" in {
    assert(Language.Java == "java")
  }
}
