package io.github.zukkari.checks

import io.github.zukkari.base.SensorRule
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree

@Rule(key = "DataClump")
class DataClump extends JavaCheck with SensorRule {
  override def scan(f: InputFile, t: Tree): Unit = ???

  override def afterAllScanned(sensorContext: SensorContext): Unit = ???
}
