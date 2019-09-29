package io.github.zukkari.config.metadata.implicits

import cats.data.Reader
import io.circe.Json
import io.github.zukkari.config.metadata.Metadata

object Readers {
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

  implicit val metadataProjector: Projector[Metadata] = for {
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
