package io.github.zukkari.config

import io.github.zukkari.config.Rules.JavaCheckClass
import org.sonar.api.server.rule.RulesDefinition.NewRepository
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader
import org.sonar.api.utils.AnnotationUtils
import org.sonar.check.Rule

object RulesSyntax {

  implicit class RulesCreatorOps(check: JavaCheckClass) {
    def makeRule(implicit repo: NewRepository, loader: RulesDefinitionAnnotationLoader): Unit = loader.load(repo, check)
  }

  implicit class AnnotationOps(checkClass: JavaCheckClass) {
    def annotation: Option[Rule] = Option(AnnotationUtils.getAnnotation(checkClass, classOf[Rule]))
  }

}
