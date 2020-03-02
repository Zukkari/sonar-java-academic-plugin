package io.github.zukkari.checks

import java.util.Optional

import io.github.zukkari.RuleSpec
import io.github.zukkari.config.ConfigurationProperties
import org.mockito.MockitoSugar._
import org.sonar.api.config.Configuration

class GodClassSpec extends RuleSpec {
  override def dir: String = "god_class"

  private val rule = new GodClass

  it should "detect god classes" in {
    verifyRule(rule, "GodClass")
  }

  override def config: Configuration = {
    val config = mock[Configuration]

    when(
      config.getInt(
        eqTo(ConfigurationProperties.GOD_CLASS_ACCESS_TO_FOREIGN_DATA.key)))
      .thenAnswer(Optional.empty[Integer]())

    when(
      config.getInt(eqTo(ConfigurationProperties.GOD_CLASS_TIGHT_COHESION.key)))
      .thenAnswer(Optional.empty[Integer]())

    when(
      config.getInt(
        eqTo(ConfigurationProperties.GOD_CLASS_CLASS_COMPLEXITY.key)))
      .thenAnswer(Optional.of[Integer](13))
  }
}
