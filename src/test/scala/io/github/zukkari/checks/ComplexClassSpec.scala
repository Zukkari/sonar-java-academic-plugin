package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class ComplexClassSpec extends RuleSpec {
  override def dir: String = "complex_class"

  val rule = new ComplexClass

  it should "detect complex classes" in {
    verifyRule(rule, "ComplexClass")
  }
}
