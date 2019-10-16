package io.github.zukkari.checks

import cats.implicits._
import cats.kernel.Monoid
import io.github.zukkari.BaseSpec
import org.sonar.java.checks.verifier.JavaCheckVerifier

class MessageChainSpec extends BaseSpec {
  val m: Monoid[String] = Monoid[String]
  val rule = new MessageChainRule

  it should "detect method call chains longer than allowed" in {
    verify("MessageChain")
  }

  def verify(check: String): Unit = {
    JavaCheckVerifier.verify(s"src/test/resources/files/message_chains/$check.java", rule)
  }
}
