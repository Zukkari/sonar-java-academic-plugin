package io.github.zukkari.config

import io.github.zukkari.BaseSpec

class LanguageSpec extends BaseSpec {
  "Language" should "contain Java" in {
    assert(Language.Java == "java")
  }
}
