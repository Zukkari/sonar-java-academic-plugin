package io.github.zukkari.checks

import cats.implicits._
import cats.kernel.Monoid
import io.github.zukkari.RuleSpec

class MessageChainSpec extends RuleSpec {
  val m: Monoid[String] = Monoid[String]
  val rule = new MessageChainRule

  it should "detect method call chains longer than allowed" in {
    verifyRule(rule, "MessageChain")
  }

  override def dir: String = "message_chains"
}
