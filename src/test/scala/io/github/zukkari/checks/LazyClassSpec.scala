package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class LazyClassSpec extends RuleSpec {
  val rule = new LazyClass

  override def dir: String = "lazy_class"

  it should "detect basic case of 0 methods" in {
    verifyRule(rule, "LazyClassBasic")
  }

  it should "detect case where class has small number of low complexity methods" in {
    verifyRule(rule, "LazyClassInstructions")
  }

  it should "detect case where coupling is low and depth of inheritance is high" in {
    verifyRule(rule, "LazyClassCoupling")
  }
}
