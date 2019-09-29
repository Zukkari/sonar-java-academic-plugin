package io.github.zukkari.definition

import cats.syntax.either._
import io.github.zukkari.config.Rules.JavaCheckClass
import io.github.zukkari.config.{Language, Rules}
import io.github.zukkari.config.RulesSyntax._
import org.sonar.api.server.rule.RulesDefinition.{NewRepository, NewRule}
import org.sonar.api.server.rule.{RulesDefinition, RulesDefinitionAnnotationLoader}
import org.sonar.check.Rule

final class AndroidRulesDefinition extends RulesDefinition {
  val repoKey = "sonar-android-key"
  val repoName = "Sonar Android repository"

  implicit val rulesLoader: RulesDefinitionAnnotationLoader = new RulesDefinitionAnnotationLoader

  override def define(context: RulesDefinition.Context): Unit = {
    implicit val repo: NewRepository = context
      .createRepository(repoKey, Language.Java)
      .setName(repoName)

    Rules.get.foreach(addRule(_))
  }

  def addRule(check: JavaCheckClass)(implicit repo: NewRepository): Either[String, NewRule] = {
    check.makeRule

    for {
      annotation <- getRuleAnnotation(check)
      key <- getAnnotationKey(annotation)
      rule <- getRule(key)
    } yield rule
  }

  def getRuleAnnotation(check: JavaCheckClass): Either[String, Rule] =
    check.annotation.asRight.ensure(s"No Rule annotation found for $check")(_ != null)

  def getAnnotationKey(annotation: Rule): Either[String, String] =
    annotation.key.asRight.ensure(s"No key is defined for annotation $annotation")(_ != null)

  def getRule(key: String)(implicit repo: NewRepository): Either[String, NewRule] =
    repo.rule(key).asRight.ensure(s"No rule was created for $key")(_ != null)
}
