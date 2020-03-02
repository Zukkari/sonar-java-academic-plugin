package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class ClassStatsCollectorSpec extends RuleSpec {
  override def dir: String = "class_stats_collector"

  private val collector = new ClassStatsCollector

  it should "write class and method statistics as JSON" in {
    verifyRule(collector, "ClassStats")
  }
}
