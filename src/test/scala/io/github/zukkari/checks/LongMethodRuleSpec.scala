package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class LongMethodRuleSpec extends RuleSpec {

  val rule = new LongMethodRule

  it should "detect method with maximum number of statements or expressions exceeded" in {
    verifyRule(rule, "LongMethod")
  }

  override def dir: String = "long_method"
}
