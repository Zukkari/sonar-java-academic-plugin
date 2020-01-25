package io.github.zukkari.checks

import io.github.zukkari.base.SensorRule
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree

@Rule(key = "StableAbstractionBreaker")
class StableAbstractionBreaker(val unstableDependencies: UnstableDependencies)
    extends JavaCheck
    with SensorRule {
  override def scan(t: Tree): Unit = ???

  override def afterAllScanned(sensorContext: SensorContext): Unit = ???
}
