package io.github.zukkari.sensor

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import org.scalatest.flatspec.AnyFlatSpec
import org.sonar.api.batch.fs.internal.TestInputFileBuilder
import org.sonar.api.batch.sensor.internal.SensorContextTester
import org.sonar.api.batch.sensor.issue.Issue

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

class SonarAcademicSensorSpec extends AnyFlatSpec {
  val sensor = new SonarAcademicSensor

  it should "detect cyclic dependencies" in {
    @tailrec
    def verifyRules(acc: Int, issues: List[Issue]): Unit = {
      issues match {
        case x :: xs if acc == 0 =>
          val line = x.primaryLocation.textRange.start.line
          assert(line == 1)
          verifyRules(acc + 1, xs)
        case x :: xs if acc == 1 =>
          val line = x.primaryLocation.textRange.start.line
          assert(line == 10)
          verifyRules(acc + 1, xs)
        case x :: xs if acc == 2 =>
          val line = x.primaryLocation.textRange.start.line
          assert(line == 18)
          verifyRules(acc + 1, xs)
        case _ =>
      }
    }

    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val inputFile = TestInputFileBuilder
      .create("", "./src/test/resources/files/cyclic_dependencies/CyclicDependencies.java")
      .setLines(25)
      .setOriginalLineEndOffsets(Array.fill(25)(0))
      .setOriginalLineStartOffsets(Array.fill(25)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues().asScala.toList

    assertResult(3) {
      issues.size
    }

    verifyRules(0, issues)
  }

  it should "detect tradition breakers" in {
    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val inputFile = TestInputFileBuilder
      .create("", "./src/test/resources/files/tradition_breaker/TraditionBreaker.java")
      .setLines(25)
      .setOriginalLineEndOffsets(Array.fill(25)(0))
      .setOriginalLineStartOffsets(Array.fill(25)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues()
      .asScala
      .toList

    assertResult(1) {
      issues.size
    }

    val issue = issues.head

    assert(issue.primaryLocation.textRange.start.line == 1)
    assert(issue.primaryLocation.message == "Tradition breaker")
  }
}
