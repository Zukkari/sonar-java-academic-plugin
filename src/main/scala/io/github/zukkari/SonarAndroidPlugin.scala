package io.github.zukkari

import io.github.zukkari.definition.AndroidRulesDefinition
import org.sonar.api.Plugin
import org.sonar.api.config.PropertyDefinition

final class SonarAndroidPlugin extends Plugin {

  override def define(context: Plugin.Context): Unit = {
    // server extensions
    context.addExtension(classOf[AndroidRulesDefinition])

    // Property definitions
    PropertyDefinition.builder("sonar.android.plugin.message.chain.length")
      .name("Message chain length")
      .description("Maximum length of message chain")
      .defaultValue("2")
      .build()
  }

}
