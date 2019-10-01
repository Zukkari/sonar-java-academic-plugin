package io.github.zukkari.config

import io.github.zukkari.BaseSpec

class LanguageKindSpec extends BaseSpec {
  "Language" should "contain Java" in {
    assert(Java.key == "java")
  }
}
