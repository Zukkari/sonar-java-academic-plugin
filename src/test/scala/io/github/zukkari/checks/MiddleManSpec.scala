package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class MiddleManSpec extends RuleSpec {
  override def dir: String = "middle_man"

  val check = new MiddleMan

  it should "detect middle man classes" in {
    verifyRule(check, "MiddleMan")
  }
}
