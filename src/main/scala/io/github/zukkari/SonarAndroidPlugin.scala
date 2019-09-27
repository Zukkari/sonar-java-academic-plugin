package io.github.zukkari

import io.github.zukkari.definition.AndroidRulesDefinition
import org.sonar.api.Plugin

final class SonarAndroidPlugin extends Plugin {

  override def define(context: Plugin.Context): Unit = {
    // server extensions
    context.addExtension(classOf[AndroidRulesDefinition])
  }

}
