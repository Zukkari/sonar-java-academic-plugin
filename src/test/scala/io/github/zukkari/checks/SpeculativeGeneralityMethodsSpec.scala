package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class SpeculativeGeneralityMethodsSpec extends RuleSpec {
  override def dir: String = "speculative_generality_methods"

  val check = new SpeculativeGeneralityMethods

  it should "detect speculative generality in method implementations" in {
    verifyRule(check, "SpeculativeGeneralityMethods")
  }
}
