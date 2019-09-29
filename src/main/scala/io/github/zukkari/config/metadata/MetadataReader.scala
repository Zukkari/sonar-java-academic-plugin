package io.github.zukkari.config.metadata

import io.circe.ParsingFailure
import io.circe.parser._
import io.github.zukkari.config.metadata.implicits.Projector

import scala.io.BufferedSource

case class Metadata(title: String,
                    typeOfRule: String,
                    status: String,
                    defaultSeverity: String,
                    tags: List[String],
                   )

object MetadataReader {

  def read[A](lineGen: () => BufferedSource)(implicit projector: Projector[A]): Either[ParsingFailure, A] =
    fromString(resource(lineGen))

  def fromString[A](s: String)(implicit projector: Projector[A]): Either[ParsingFailure, A] =
    for {
      json <- parse(s)
    } yield projector.run(json)

  def resource(fn: () => BufferedSource): String = fn().getLines.fold("")(_ ++ _)

}
