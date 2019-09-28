package io.github.zukkari.definition

import io.github.zukkari.config.{Language, Rules}
import io.github.zukkari.config.RulesSyntax._
import org.sonar.api.server.rule.RulesDefinition.{NewRepository, NewRule}
import org.sonar.api.server.rule.{RulesDefinition, RulesDefinitionAnnotationLoader}

final class AndroidRulesDefinition extends RulesDefinition {
  val repoKey = "sonar-android-key"
  val repoName = "Sonar Android repository"

  implicit val rulesLoader: RulesDefinitionAnnotationLoader = new RulesDefinitionAnnotationLoader

  override def define(context: RulesDefinition.Context): Unit = {
    implicit val repo: NewRepository = context
      .createRepository(repoKey, Language.Java)
      .setName(repoName)

    Rules.get
      .map(check => {
        check.makeRule
        addRule(check)
      })
  }

  def addRule(check: Rules.JavaCheckClass)(implicit repo: NewRepository): Either[String, NewRule] = {
    for {
      ruleAnnotation <- check.annotation.toRight(s"No Rule annotation found for $check")
      key <- Option(ruleAnnotation.key).toRight(s"No key is defined for annotation $ruleAnnotation")
      rule <- Option(repo.rule(key)).toRight(s"No rule was created for $key")
    } yield rule
  }

}
