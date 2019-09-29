package io.github.zukkari.config.metadata

import cats.data.Reader
import io.circe.Json
import org.sonar.api.server.rule.RulesDefinition.NewRule

import scala.io.{BufferedSource, Source}

package object implicits {
  type Resource[A] = A => BufferedSource
  implicit val ruleResource: Resource[NewRule] = rule => Source.fromResource(s"metadata/${rule.key}_java.json")

  type Projector[A] = Reader[Json, A]
  implicit val metaProjector: Projector[Metadata] = Readers.metadataProjector

}
