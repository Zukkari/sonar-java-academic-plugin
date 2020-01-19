package io.github.zukkari.checks

import java.util.Optional

import io.github.zukkari.RuleSpec
import org.sonar.api.config.Configuration
import org.mockito.MockitoSugar.when

class SwissArmyKnifeSpec extends RuleSpec {
  override def dir: String = "swiss_army_knife"

  private val rule = new SwissArmyKnife

  it should "detect interfaces which has too many methods" in {
    verifyRule(rule, "SwissArmyKnife")
  }

  override def config: Configuration = {
    val config = mock[Configuration]

    when(config.getInt(any)).thenAnswer(Optional.of[Integer](5))

    config
  }
}
