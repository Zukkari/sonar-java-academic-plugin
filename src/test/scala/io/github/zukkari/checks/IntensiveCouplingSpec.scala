package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class IntensiveCouplingSpec extends RuleSpec {
  override def dir: String = "intensive_coupling"

  private val rule = new IntensiveCoupling

  it should "detect intensive coupling when number of methods is higher than short memory cap" in {
    verifyRule(rule, "IntensiveCouplingShortCap")
  }

  it should "detect intensive coupling with coupling with external methods is too high" in {
    verifyRule(rule, "IntensiveCouplingTightCoupling")
  }
}
