package io.github.zukkari

import cats.Monoid
import cats.data.Reader
import io.circe.Json
import io.github.zukkari.checks.Traversal
import io.github.zukkari.config.DirectoryKind
import io.github.zukkari.config.metadata.Metadata
import org.sonar.api.server.rule.RulesDefinition.NewRule

import scala.io.{BufferedSource, Source}

package object implicits {
  type Resource[A, B] = (A, B) => BufferedSource

  implicit def ruleResource[A <: DirectoryKind]: Resource[A, NewRule] = (dir, rule) => Source.fromResource(s"${dir.name}/${rule.key}_java.${dir.ext}")

  type Projector[A] = Reader[Json, A]
  implicit val metaProjector: Projector[Metadata] = Readers.metadataProjector

  implicit def traversalMonoid(implicit m: Monoid[String]): Monoid[Traversal] = new Monoid[Traversal] {
    override def empty: Traversal = Traversal(m.empty, 1)

    override def combine(x: Traversal, y: Traversal): Traversal =
      Traversal(x.methodName ++ " -> " ++ y.methodName, x.depth + y.depth)
  }
}
