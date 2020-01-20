package io.github.zukkari.checks

import io.github.zukkari.base.SensorRule
import io.github.zukkari.config.ConfigurationProperties
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Configuration
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree

@Rule(key = "MissingTemplateMethod")
class MissingTemplateMethod extends JavaCheck with SensorRule {

  private var minimalCommonVariableAndMethodCount: Int = _
  private var minimalMethodCount: Int = _

  override def configure(configuration: Configuration): Unit = {
    minimalCommonVariableAndMethodCount = configuration
      .getInt(
        ConfigurationProperties.MISSING_TEMPLATE_METHOD_COMMON_MEMBERS.key)
      .orElse(
        ConfigurationProperties.MISSING_TEMPLATE_METHOD_COMMON_MEMBERS.defaultValue.toInt)

    minimalMethodCount = configuration
      .getInt(
        ConfigurationProperties.MISSING_TEMPLATE_METHOD_COMMON_METHODS.key
      )
      .orElse(
        ConfigurationProperties.MISSING_TEMPLATE_METHOD_COMMON_METHODS.defaultValue.toInt)
  }

  override def scan(t: Tree): Unit = ???

  override def afterAllScanned(sensorContext: SensorContext): Unit = ???
}
