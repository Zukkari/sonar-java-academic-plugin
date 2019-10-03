package io.github.zukkari.config.metadata

import cats.implicits._
import cats.{Eval, Semigroupal}
import io.github.zukkari.config.metadata.implicits.{Resource, _}
import io.github.zukkari.config.{DirectoryKind, MetadataDirectory, Template}
import org.sonar.api.rule.RuleStatus
import org.sonar.api.rules.RuleType
import org.sonar.api.server.rule.RulesDefinition
import org.sonar.api.server.rule.RulesDefinition.NewRule

trait MetadataGen[A] {
  type EitherOr[B] = Either[String, B]

  def addHtmlDescription(elem: A)(implicit gen: Resource[DirectoryKind, A]): EitherOr[A]

  def addMetadata(elem: A)(implicit gen: Resource[DirectoryKind, A]): EitherOr[A]

}

object MetadataGenInstances {
  implicit val ruleMetaGen: MetadataGen[NewRule] = new MetadataGen[NewRule] {

    override def addHtmlDescription(elem: NewRule)
                                   (implicit gen: Resource[DirectoryKind, NewRule]): EitherOr[NewRule] =
      MetadataReader.resource(Eval.later(gen(Template, elem)))
        .map(html => elem.setHtmlDescription(html))
        .value
        .asRight

    override def addMetadata(elem: NewRule)
                            (implicit genFn: Resource[DirectoryKind, NewRule]): EitherOr[NewRule] = {
      MetadataReader.read(Eval.later(genFn(MetadataDirectory, elem)))
        .bimap(_.message, apply(elem, _))
    }

    def apply(rule: RulesDefinition.NewRule, metadata: Metadata): NewRule = {
      rule
        .setName(metadata.title)
        .setSeverity(metadata.defaultSeverity)
        .addTags(metadata.tags: _*)
        .setType(RuleType.valueOf(metadata.typeOfRule))
        .setStatus(RuleStatus.valueOf(metadata.status))
    }
  }
}

object MetadataGenSyntax {
  type EitherOr[B] = Either[String, B]

  implicit class MetadataGenOps[A](elem: A) {
    def genMeta(implicit gen: MetadataGen[A],
                resource: Resource[DirectoryKind, A]): EitherOr[(A, A)] = {
      Semigroupal[EitherOr].product(
        gen.addHtmlDescription(elem),
        gen.addMetadata(elem)
      )
    }
  }

}
