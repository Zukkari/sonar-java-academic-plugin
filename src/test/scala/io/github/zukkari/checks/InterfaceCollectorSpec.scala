package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class InterfaceCollectorSpec extends RuleSpec {
  override def dir: String = "interface_stats_collector"

  private val collector = new InterfaceStatsCollector

  it should "write class and method statistics as JSON" in {
    verifyRule(collector, "InterfaceStats")
  }
}
