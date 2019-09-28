package io.github.zukkari.config

import io.github.zukkari.checks.DataClassRule
import org.scalatest.flatspec.AnyFlatSpec

class RulesSpec extends AnyFlatSpec {

  "Rules list" should "contain DataClass rule" in {
    val rules = Rules.get

    assert(rules contains classOf[DataClassRule])
  }
}
