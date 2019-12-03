package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class RefusedBequestSpec extends RuleSpec {

  override def dir: String = "refused_bequest"

  it should "detect cases where parent class has more than 'X' protected members and child class does not use them" in {
    val rule = new RefusedBequest
    verifyRule(rule, "RefusedBequest")
  }

  it should "detect cases where child class overrides some methods but methods overall are not complex enough" in {
    val rule = new RefusedBequest
    verifyRule(rule, "RefusedBequestOverride")
  }
}
