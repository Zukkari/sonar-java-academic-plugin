package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class GodClassSpec extends RuleSpec {
  override def dir: String = "god_class"

  private val rule = new GodClass(5, 0.33, 15)

  it should "detect god classes" in {
    verifyRule(rule, "GodClass")
  }
}
