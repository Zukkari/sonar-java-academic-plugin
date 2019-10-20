package io.github.zukkari.config

import io.github.zukkari.BaseSpec
import io.github.zukkari.checks.{DataClassRule, LongMethodRule, MessageChainRule}

class RulesSpec extends BaseSpec {

  it should "contain 'data class' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[DataClassRule])
  }

  it should "contain 'message chain' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[MessageChainRule])
  }

  it should "contain 'long method' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[LongMethodRule])
  }
}
