package io.github.zukkari.config

import io.github.zukkari.checks.{DataClassRule, MessageChainRule}
import io.github.zukkari.config.Rules.JavaCheckClass
import org.sonar.api.server.rule.RulesDefinition.NewRepository
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader
import org.sonar.api.utils.AnnotationUtils
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck


object Rules {
  type JavaCheckClass = Class[JavaCheck]

  def get: List[JavaCheckClass] = List(
    classOf[DataClassRule],
    classOf[MessageChainRule]
  ).map(_.asInstanceOf[JavaCheckClass])

}

object RulesSyntax {

  implicit class RulesCreatorOps(check: JavaCheckClass) {
    def makeRule(implicit repo: NewRepository, loader: RulesDefinitionAnnotationLoader): Unit = loader.load(repo, check)
  }

  implicit class AnnotationOps(checkClass: JavaCheckClass) {
    def annotation: Rule = AnnotationUtils.getAnnotation(checkClass, classOf[Rule])
  }

}
