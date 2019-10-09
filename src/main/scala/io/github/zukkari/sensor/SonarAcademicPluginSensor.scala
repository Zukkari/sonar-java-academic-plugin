package io.github.zukkari.sensor

import io.github.zukkari.config.Java
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import io.github.zukkari.util.Log
import org.sonar.api.batch.sensor.{Sensor, SensorContext, SensorDescriptor}

final class SonarAcademicPluginSensor extends Sensor {
  private val log = Log(classOf[SonarAcademicPluginSensor])

  override def execute(context: SensorContext): Unit = {
    log.info(() => "Running execute method")
  }

  override def describe(descriptor: SensorDescriptor): Unit = {
    log.info(() => "Running describe method")

    descriptor
      .onlyOnLanguage(Java.key)
      .name("Sonar academic plugin sensor")
      .createIssuesForRuleRepository(SonarAcademicRulesDefinition.repoKey)
  }

}
