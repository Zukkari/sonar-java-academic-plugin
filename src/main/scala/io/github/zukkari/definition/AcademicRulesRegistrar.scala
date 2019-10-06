package io.github.zukkari.definition

import io.github.zukkari.config.Rules
import org.sonar.plugins.java.api.CheckRegistrar
import org.sonarsource.api.sonarlint.SonarLintSide
import io.github.zukkari.definition.AcademicRulesDefinition._
import scala.jdk.CollectionConverters._

@SonarLintSide
class AcademicRulesRegistrar extends CheckRegistrar {
  override def register(registrarContext: CheckRegistrar.RegistrarContext): Unit =
    registrarContext.registerClassesForRepository(repoKey, Rules.get.asJava, Nil.asJava)
}
