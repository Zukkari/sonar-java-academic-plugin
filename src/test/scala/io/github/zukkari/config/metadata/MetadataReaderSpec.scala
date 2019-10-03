package io.github.zukkari.config.metadata

import cats.Eval
import cats.data.Reader
import cats.implicits._
import io.github.zukkari.BaseSpec
import io.github.zukkari.implicits.Projector
import org.mockito.MockitoSugar._

import scala.io.BufferedSource

class MetadataReaderSpec extends BaseSpec {
  implicit val reader: Projector[Int] = Reader(json => json.hcursor.downField("x").focus.get.asNumber.flatMap(_.toInt).getOrElse(-1))

  it should "correctly parse valid JSON" in {
    val json =
      """{
        |"x": 1
        |}
        |""".stripMargin

    MetadataReader.fromString(Eval.now(json)) match {
      case Left(err) => fail(err)
      case _ => succeed
    }
  }

  it should "fail with invalid JSON" in {
    val json = "invalid json"

    MetadataReader.fromString(Eval.now(json)) match {
      case Left(_) => succeed
      case _ => fail("Parsed invalid JSON successfully?")
    }
  }

  it should "convert lines to String" in {
    val src = mock[BufferedSource]
    when(src.getLines).thenReturn(List("1", "2", "3").iterator)

    assert(MetadataReader.resource(Eval.now(src)).value == "123")
  }
}
