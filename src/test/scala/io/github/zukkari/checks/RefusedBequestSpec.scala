package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class RefusedBequestSpec extends RuleSpec {
  val rule = new RefusedBequest

  override def dir: String = "refused_bequest"

  it should "detect cases where child class does not use parents protected methods" in  {
    verifyRule(rule, "RefusedBequest")
  }
}
