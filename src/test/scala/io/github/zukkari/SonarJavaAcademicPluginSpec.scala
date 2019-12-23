package io.github.zukkari

import io.github.zukkari.definition.{SonarAcademicRulesDefinition, SonarAcademicRulesRegistrar}
import io.github.zukkari.sensor.SonarAcademicSensor
import org.sonar.api.internal.SonarRuntimeImpl
import org.sonar.api.utils.Version
import org.sonar.api.{Plugin, SonarEdition, SonarQubeSide, SonarRuntime}

class SonarJavaAcademicPluginSpec extends BaseSpec {
  val runtime: SonarRuntime = SonarRuntimeImpl.forSonarQube(
    Version.create(7, 9),
    SonarQubeSide.SCANNER,
    SonarEdition.COMMUNITY
  )

  val context = new Plugin.Context(runtime)

  "Android plugin" should "should add defined rules to context" in {
    new SonarJavaAcademicPlugin().define(context)

    assert(context.getExtensions.size() == 3)

    // contains only extensions that were added
    assert(context.getExtensions.contains(classOf[SonarAcademicRulesDefinition]))
    assert(context.getExtensions.contains(classOf[SonarAcademicRulesRegistrar]))
    assert(context.getExtensions.contains(classOf[SonarAcademicSensor]))
  }
}
