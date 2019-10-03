package io.github.zukkari.config.metadata

import cats.implicits._
import cats.{Eval, Monoid}
import io.circe.ParsingFailure
import io.circe.parser._
import io.github.zukkari.implicits.Projector

import scala.io.BufferedSource

case class Metadata(title: String,
                    typeOfRule: String,
                    status: String,
                    defaultSeverity: String,
                    tags: List[String],
                   )

object MetadataReader {

  def read[A](lineGen: Eval[BufferedSource])(implicit projector: Projector[A]): Either[ParsingFailure, A] =
    fromString(resource(lineGen))

  def fromString[A](context: Eval[String])(implicit projector: Projector[A]): Either[ParsingFailure, A] =
    for {
      json <- parse(context.value)
    } yield projector.run(json)

  def resource(gen: Eval[BufferedSource])
              (implicit m: Monoid[String]): Eval[String] = gen.map(_.getLines.fold(m.empty)(_ ++ _))

}
