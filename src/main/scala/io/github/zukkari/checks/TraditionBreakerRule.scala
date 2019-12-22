package io.github.zukkari.checks

import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck


@Rule(key = "TraditionBreakerRule")
class TraditionBreakerRule extends JavaCheck

object TraditionBreakerRule {
  val key = "TraditionBreakerRule"
}
