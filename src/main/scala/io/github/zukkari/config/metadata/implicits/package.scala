package io.github.zukkari.config.metadata

import cats.data.Reader
import io.circe.Json
import io.github.zukkari.config.DirectoryKind
import org.sonar.api.server.rule.RulesDefinition.NewRule

import scala.io.{BufferedSource, Source}

package object implicits {
  type Resource[A, B] = (A, B) => BufferedSource
  implicit def ruleResource[A <: DirectoryKind]: Resource[A, NewRule] = (dir, rule) => Source.fromResource(s"${dir.name}/${rule.key}_java.${dir.ext}")

  type Projector[A] = Reader[Json, A]
  implicit val metaProjector: Projector[Metadata] = Readers.metadataProjector

}
