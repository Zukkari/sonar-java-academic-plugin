package io.github.zukkari.definition

import io.github.zukkari.config.Rules
import io.github.zukkari.definition.SonarAcademicRulesDefinition._
import org.sonar.plugins.java.api.CheckRegistrar
import org.sonarsource.api.sonarlint.SonarLintSide

import scala.jdk.CollectionConverters._

@SonarLintSide
class SonarAcademicRulesRegistrar extends CheckRegistrar {
  override def register(registrarContext: CheckRegistrar.RegistrarContext): Unit =
    registrarContext.registerClassesForRepository(repoKey, Rules.get.asJava, Nil.asJava)
}
