package io.github.zukkari.checks

import io.github.zukkari.BaseSpec
import org.sonar.java.checks.verifier.JavaCheckVerifier

class MessageChainSpec extends BaseSpec {

  it should "detect method call chains in the same file" in {
    verify("MessageChain")
  }

  def verify(check: String): Unit = {
    JavaCheckVerifier.verify(s"src/test/resources/files/message_chains/${check}.java", new MessageChainRule())
  }
}
