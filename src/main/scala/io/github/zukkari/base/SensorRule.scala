package io.github.zukkari.base

import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.plugins.java.api.tree.Tree

trait SensorRule {

  def scan(f: InputFile, t: Tree): Unit

  def afterAllScanned(sensorContext: SensorContext): Unit
}
