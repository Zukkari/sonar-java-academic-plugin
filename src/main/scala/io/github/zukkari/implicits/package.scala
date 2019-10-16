package io.github.zukkari

import cats.Monoid
import cats.data.Reader
import io.circe.Json
import io.github.zukkari.checks.Chain
import io.github.zukkari.config.DirectoryKind
import io.github.zukkari.config.metadata.Metadata
import org.sonar.api.server.rule.RulesDefinition.NewRule

import scala.io.{BufferedSource, Source}

package object implicits {
  type Resource[A, B] = (A, B) => BufferedSource

  implicit def ruleResource[A <: DirectoryKind]: Resource[A, NewRule] =
    (dir, rule) => Source.fromResource(s"${dir.name}/${rule.key}_java.${dir.ext}", classOf[SonarJavaAcademicPlugin].getClassLoader)

  type Projector[A] = Reader[Json, A]
  implicit val metaProjector: Projector[Metadata] = Readers.metadataProjector

  implicit val traversalMonoid: Monoid[Chain] = new Monoid[Chain] {
    override def empty: Chain = Chain(1)

    override def combine(x: Chain, y: Chain): Chain =
      Chain(x.depth + y.depth)
  }
}
