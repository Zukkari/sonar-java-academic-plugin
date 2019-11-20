package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class LongParameterListSpec extends RuleSpec {
  val rule = new LongParameterList

  override def dir: String = "long_parameter_list"

  it should "detect long parameter list" in {
    verifyRule(rule, "LongParameterList")
  }
}
