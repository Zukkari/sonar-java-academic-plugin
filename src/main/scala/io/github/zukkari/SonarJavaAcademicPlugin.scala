package io.github.zukkari

import io.github.zukkari.config.{ConfigurationProperties, ConfigurationProperty}
import io.github.zukkari.definition.{
  SonarAcademicRulesDefinition,
  SonarAcademicRulesRegistrar
}
import io.github.zukkari.sensor.SonarAcademicSensor
import io.github.zukkari.util.Log
import org.sonar.api.Plugin
import org.sonar.api.config.PropertyDefinition

final class SonarJavaAcademicPlugin extends Plugin {
  private val log = Log(classOf[SonarJavaAcademicPlugin])

  override def define(context: Plugin.Context): Unit = {
    log.info(s"Started $this plugin initialization")

    // server extensions
    context.addExtension(classOf[SonarAcademicRulesDefinition])

    // registrar
    context.addExtension(classOf[SonarAcademicRulesRegistrar])

    context.addExtension(classOf[SonarAcademicSensor])

    // Property definitions
    ConfigurationProperties.properties
      .map {
        case ConfigurationProperty(key, description, name, defaultValue) =>
          PropertyDefinition
            .builder(key)
            .name(name)
            .description(description)
            .defaultValue(defaultValue)
            .build()
      }
      .foreach(context.addExtension)

    log.info(s"Finished $this plugin initialization")
  }

}
