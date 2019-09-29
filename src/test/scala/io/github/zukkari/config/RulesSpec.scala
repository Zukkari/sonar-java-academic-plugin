package io.github.zukkari.config

import io.github.zukkari.BaseSpec
import io.github.zukkari.checks.DataClassRule

class RulesSpec extends BaseSpec {

  "Rules list" should "contain DataClass rule" in {
    val rules = Rules.get

    assert(rules contains classOf[DataClassRule])
  }
}
