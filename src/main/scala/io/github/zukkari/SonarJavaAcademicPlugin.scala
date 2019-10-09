package io.github.zukkari

import io.github.zukkari.definition.{SonarAcademicRulesDefinition, SonarAcademicRulesRegistrar}
import io.github.zukkari.sensor.SonarAcademicPluginSensor
import io.github.zukkari.util.Log
import org.sonar.api.Plugin
import org.sonar.api.config.PropertyDefinition

final class SonarJavaAcademicPlugin extends Plugin {
  private val log = Log(classOf[SonarJavaAcademicPlugin])

  override def define(context: Plugin.Context): Unit = {
    log.info(() => s"Started $this plugin initialization")

    // server extensions
    context.addExtension(classOf[SonarAcademicRulesDefinition])

    // registrar
    context.addExtension(classOf[SonarAcademicRulesRegistrar])

    // Property definitions
    PropertyDefinition.builder("sonar.android.plugin.message.chain.length")
      .name("Message chain length")
      .description("Maximum length of message chain")
      .defaultValue("2")
      .build()

    log.info(() => s"Finished $this plugin initialization")
  }

}
