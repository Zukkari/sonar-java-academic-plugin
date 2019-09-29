package io.github.zukkari.config.optics

import cats.data.Reader
import io.circe.Json
import io.github.zukkari.config.metadata.Metadata

object OpticsInstances {
  type ErrorOr[A] = Either[String, A]

  val titleReader: Reader[Json, String] = fieldReader("title")
  val typeReader: Reader[Json, String] = fieldReader("type")
  val statusReader: Reader[Json, String] = fieldReader("status")
  val severityReader: Reader[Json, String] = fieldReader("defaultSeverity")

  def fieldReader(name: String): Reader[Json, String] = Reader(json => json.hcursor
    .get[String](name)
    .toOption
    .getOrElse("")
  )

  val tagsReader: Reader[Json, List[String]] = Reader(
    json =>
      json.hcursor
      .downField("tags")
      .focus
      .flatMap(_.asArray)
      .getOrElse(Vector.empty)
      .map(_.asString.getOrElse(""))
      .toList
  )

  implicit val metaReader: Reader[Json, Metadata] = for {
    ruleTitle <- titleReader
    ruleType <- typeReader
    ruleStatus <- statusReader
    ruleSeverity <- severityReader
    ruleTags <- tagsReader
  } yield Metadata(
    title = ruleTitle,
    typeOfRule = ruleType,
    status = ruleStatus,
    defaultSeverity = ruleSeverity,
    tags = ruleTags
  )
}

object OpticsSyntax {

  implicit class OpticsOps(val json: Json) {
    def project[A](implicit reader: Reader[Json, A]): A = reader.run(json)
  }

}
