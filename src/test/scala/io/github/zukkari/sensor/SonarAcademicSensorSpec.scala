package io.github.zukkari.sensor

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import io.github.zukkari.checks._
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import org.scalatest.flatspec.AnyFlatSpec
import org.sonar.api.batch.fs.internal.TestInputFileBuilder
import org.sonar.api.batch.sensor.internal.{
  DefaultSensorDescriptor,
  SensorContextTester
}
import org.sonar.api.batch.sensor.issue.Issue

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

class SonarAcademicSensorSpec extends AnyFlatSpec {

  it should "contain proper sensor description" in {
    val sensor = new SonarAcademicSensor

    val descriptor = new DefaultSensorDescriptor
    sensor.describe(descriptor)

    assert(descriptor.name != null && descriptor.name.length > 0)
    assert(descriptor.languages contains "java")
    assert(
      descriptor.ruleRepositories contains SonarAcademicRulesDefinition.repoKey)
  }

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

    val sensor = new SonarAcademicSensor(List(new CyclicDependenciesRule))

    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val inputFile = TestInputFileBuilder
      .create(
        "",
        "./src/test/resources/files/cyclic_dependencies/CyclicDependencies.java")
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
    val lines = 49
    val inputFile = TestInputFileBuilder
      .create(
        "",
        "./src/test/resources/files/tradition_breaker/TraditionBreaker.java")
      .setLines(lines)
      .setOriginalLineEndOffsets(Array.fill(lines)(0))
      .setOriginalLineStartOffsets(Array.fill(lines)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    val sensor = new SonarAcademicSensor(List(new TraditionBreakerRule))

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues().asScala.toList

    assertResult(1) {
      issues.size
    }

    val issue = issues.head

    assert(issue.primaryLocation.textRange.start.line == 1)
    assert(issue.primaryLocation.message == "Tradition breaker")
  }

  it should "detect data clumps" in {
    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val lines = 12
    val inputFile = TestInputFileBuilder
      .create("", "./src/test/resources/files/data_clump/DataClump.java")
      .setLines(lines)
      .setOriginalLineEndOffsets(Array.fill(lines)(0))
      .setOriginalLineStartOffsets(Array.fill(lines)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    val sensor = new SonarAcademicSensor(List(new DataClump))

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues().asScala.toList

    assertResult(2) {
      issues.size
    }

    issues match {
      case first :: second :: _ =>
        assert(first.primaryLocation.textRange.start.line == 1)
        assert(
          first.primaryLocation.message == "Data clump: similar to class: 'Service'")

        assert(second.primaryLocation.textRange.start.line == 7)
        assert(
          second.primaryLocation.message == "Data clump: similar to class: 'DataClump'")
      case _ =>
        fail("Hello Mr compiler")
    }
  }

  it should "detect parallel inheritance hierarchies" in {
    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val lines = 42
    val inputFile = TestInputFileBuilder
      .create(
        "",
        "./src/test/resources/files/parallel_inheritance_hierarchies/ParallelHierarchy.java")
      .setLines(lines)
      .setOriginalLineEndOffsets(Array.fill(lines)(0))
      .setOriginalLineStartOffsets(Array.fill(lines)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    val sensor =
      new SonarAcademicSensor(List(new ParallelInheritanceHierarchies))

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues().asScala.toList

    assertResult(2) {
      issues.size
    }

    issues match {
      case first :: second :: _ =>
        assert(first.primaryLocation.textRange.start.line == 1)
        assert(
          first.primaryLocation.message == "Parallel hierarchy with class: 'ParallelAlternative'")

        assert(second.primaryLocation.textRange.start.line == 5)
        assert(
          second.primaryLocation.message == "Parallel hierarchy with class: 'ParallelHierarchy'")
      case _ =>
        fail("Hello Mr compiler")
    }
  }

  it should "detect speculative generality in interface implementations" in {
    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val lines = 20
    val inputFile = TestInputFileBuilder
      .create(
        "",
        "./src/test/resources/files/speculative_generality_interfaces/SpeculativeGeneralityInterfaces.java")
      .setLines(lines)
      .setOriginalLineEndOffsets(Array.fill(lines)(0))
      .setOriginalLineStartOffsets(Array.fill(lines)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    val sensor =
      new SonarAcademicSensor(List(new SpeculativeGeneralityInterfaces))

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues().asScala.toList

    assertResult(2) {
      issues.size
    }

    issues match {
      case first :: second :: _ =>
        assert(first.primaryLocation.textRange.start.line == 1)
        assert(
          first.primaryLocation.message == "Speculative generality: provide at least one implementation for this interface")

        assert(second.primaryLocation.textRange.start.line == 17)
        assert(
          second.primaryLocation.message == "Speculative generality: provide at least one implementation for this interface")
      case _ =>
        fail("Hello Mr compiler")
    }
  }

  it should "detect primitive obsession" in {
    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val lines = 20
    val inputFile = TestInputFileBuilder
      .create(
        "",
        "./src/test/resources/files/primitive_obsession/PrimitiveObsession.java")
      .setLines(lines)
      .setOriginalLineEndOffsets(Array.fill(lines)(0))
      .setOriginalLineStartOffsets(Array.fill(lines)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    val sensor = new SonarAcademicSensor(List(new PrimitiveObsession))

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues().asScala.toList

    assertResult(1) {
      issues.size
    }

    val issue = issues.head
    assert(issue.primaryLocation.textRange.start.line == 2)
    assert(
      issue.primaryLocation.message == "Primitive obsession: externally declared class used 4 times with max allowed 3")
  }

  it should "detect brain methods" in {
    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val lines = 51
    val inputFile = TestInputFileBuilder
      .create("", "./src/test/resources/files/brain_method/BrainMethod.java")
      .setLines(lines)
      .setOriginalLineEndOffsets(Array.fill(lines)(0))
      .setOriginalLineStartOffsets(Array.fill(lines)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    val sensor = new SonarAcademicSensor(List(new BrainMethod(5, 5, 2, 2)))

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues().asScala.toList

    assertResult(4) {
      issues.size
    }

    issues match {
      case first :: second :: third :: fourth :: _ =>
        assert(first.primaryLocation.textRange.start.line == 7)
        assert(first.primaryLocation.message == "Brain method")

        assert(second.primaryLocation.textRange.start.line == 19)
        assert(second.primaryLocation.message == "Brain method")

        assert(third.primaryLocation.textRange.start.line == 29)
        assert(third.primaryLocation.message == "Brain method")

        assert(fourth.primaryLocation.textRange.start.line == 41)
        assert(fourth.primaryLocation.message == "Brain method")
      case _ =>
        fail("Hello, Mr Compiler!")
    }
  }

  it should "detect inappropriate intimacy" in {
    val context = SensorContextTester.create(Paths.get("./src/test/resources"))
    val lines = 31
    val inputFile = TestInputFileBuilder
      .create(
        "",
        "./src/test/resources/files/inappropriate_intimacy/InappropriateIntimacy.java")
      .setLines(lines)
      .setOriginalLineEndOffsets(Array.fill(lines)(0))
      .setOriginalLineStartOffsets(Array.fill(lines)(0))
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("java")
      .build()

    val sensor =
      new SonarAcademicSensor(List(new InappropriateIntimacy))

    context.fileSystem().add(inputFile)
    sensor.execute(context)

    val issues = context.allIssues().asScala.toList

    assertResult(2) {
      issues.size
    }

    issues match {
      case first :: second :: _ =>
        assert(first.primaryLocation.textRange.start.line == 1)
        assert(
          first.primaryLocation.message == "Inappropriate intimacy: number of method calls 5 with class B is greater than configured 4")

        assert(second.primaryLocation.textRange.start.line == 18)
        assert(
          second.primaryLocation.message == "Inappropriate intimacy: number of method calls 5 with class A is greater than configured 4")
      case _ =>
        fail("Hello, Mr Compiler!")
    }
  }
}
