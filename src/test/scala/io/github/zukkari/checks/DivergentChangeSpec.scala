package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class DivergentChangeSpec extends RuleSpec {
  override def dir: String = "divergent_change"

  val rule = new DivergentChange

  it should "detect Schizophrenic class in" in {
    verifyRule(rule, "DivergentChange")
  }
}
