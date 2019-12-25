package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class FeatureEnvySpec extends RuleSpec {
  override def dir: String = "feature_envy"

  private val check = new FeatureEnvy

  it should "detect feature envy" in {
    verifyRule(check, "FeatureEnvy")
  }
}
