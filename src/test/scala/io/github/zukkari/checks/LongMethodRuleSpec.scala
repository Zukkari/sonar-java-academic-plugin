package io.github.zukkari.checks

import java.util.Optional

import io.github.zukkari.RuleSpec
import org.sonar.api.config.Configuration
import org.mockito.MockitoSugar.when

class LongMethodRuleSpec extends RuleSpec {

  val rule = new LongMethodRule

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

  override def config: Configuration = {
    val config = mock[Configuration]

    when(config.getInt(any)).thenAnswer(Optional.of[Integer](2))

    config
  }

  override def dir: String = "long_method"
}
