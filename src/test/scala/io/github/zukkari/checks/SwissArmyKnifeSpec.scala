package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class SwissArmyKnifeSpec extends RuleSpec {
  override def dir: String = "swiss_army_knife"

  private val rule = new SwissArmyKnife(veryHighNumberOfMethods = 5)

  it should "detect interfaces which has too many methods" in {
    verifyRule(rule, "SwissArmyKnife")
  }

  it should "have a default constructor for Sonar" in {
    val default = new SwissArmyKnife

    assert(default.veryHighNumberOfMethods == 13)
  }
}
