package io.github.zukkari.checks

import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck


@Rule(key = "CyclicDependencies")
class CyclicDependenciesRule extends JavaCheck {
}

object CyclicDependenciesRule {
  val ruleKey = "CyclicDependencies"
}
