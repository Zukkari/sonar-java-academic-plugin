package io.github.zukkari.config.metadata

import io.circe.ParsingFailure
import io.circe.parser._
import io.github.zukkari.config.optics.OpticsInstances._
import io.github.zukkari.config.optics.OpticsSyntax._
import org.sonar.api.server.rule.RulesDefinition.NewRule

import scala.io.Source

case class Metadata(title: String,
                    typeOfRule: String,
                    status: String,
                    defaultSeverity: String,
                    tags: List[String],
                   )

object MetadataReader {
  val metaBasePath = "metadata"

  def read(rule: NewRule): Either[ParsingFailure, Metadata] = {
    val res = resource(rule)
    for {
      json <- parse(res)
    } yield json.project
  }

  def resource(rule: NewRule): String = Source.fromResource(s"${rule.key}_java.json").getLines.fold("")(_ ++ _)

}
