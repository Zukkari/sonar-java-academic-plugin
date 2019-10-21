package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class SwitchStatementRuleSpec extends RuleSpec {
  val rule = new SwitchStatementRule

  it should "detect usages of switch statements" in {
    verifyRule(rule, "SwitchStatement")
  }

  override def dir: String = "switch_statement"
}
