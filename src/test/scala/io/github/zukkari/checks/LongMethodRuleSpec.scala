package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class LongMethodRuleSpec extends RuleSpec {

  val rule = new LongMethodRule
  rule.methodLength = 2

  it should "detect method with maximum number of statements or expressions exceeded" in {
    verifyRule(rule, "Base")
  }

  it should "detect exceeded limit in if statements" in {
    verifyRule(rule, "Branch")
  }

  it should "detect exceeded limit in do while loop" in {
    verifyRule(rule, "DoWhileLoop")
  }

  it should "detect exceeded limit in for loop" in {
    verifyRule(rule, "ForLoop")
  }

  it should "detect exceeded limit in synchronized blocks" in {
    verifyRule(rule, "SynchronizedBlock")
  }

  it should "detect exceeded limit in try blocks" in {
    verifyRule(rule, "TryBlock")
  }

  it should "detect exceeded limit in while loops" in {
    verifyRule(rule, "WhileLoop")
  }

  override def dir: String = "long_method"
}
