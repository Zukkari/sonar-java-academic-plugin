package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class ShotgunSurgeryRuleSpec extends RuleSpec {
  val check = new ShotgunSurgeryRule()

  it should "detect Shotgun Surgery rule" in {
    verifyRule(check, "ShotgunSurgery")
  }

  override def dir: String = "shotgun_surgery"
}
