package io.github.zukkari.sensor

import io.github.zukkari.base.SensorRule
import io.github.zukkari.checks.{
  BrainMethod,
  CyclicDependenciesRule,
  DataClump,
  InappropriateIntimacy,
  ParallelInheritanceHierarchies,
  PrimitiveObsession,
  SpeculativeGeneralityInterfaces,
  TraditionBreakerRule
}
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import io.github.zukkari.util.Log
import org.sonar.api.batch.sensor.{Sensor, SensorContext, SensorDescriptor}
import org.sonar.api.utils.log.Loggers
import org.sonar.java.ast.parser.JavaParser

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

class SonarAcademicSensor(val rules: List[SensorRule]) extends Sensor {
  private val log = Log(this.getClass)

  def this() =
    this(
      List(
        new CyclicDependenciesRule,
        new TraditionBreakerRule,
        new DataClump,
        new ParallelInheritanceHierarchies,
        new SpeculativeGeneralityInterfaces,
        new PrimitiveObsession,
        new BrainMethod,
        new InappropriateIntimacy
      ))

  override def describe(descriptor: SensorDescriptor): Unit = {
    descriptor.name("Sensor Sonar Academic Plugin")
    descriptor.onlyOnLanguage("java")
    descriptor.createIssuesForRuleRepository(
      SonarAcademicRulesDefinition.repoKey)
  }

  override def execute(context: SensorContext): Unit = {
    log.info("Executing cyclic dependencies check")

    val fs = context.fileSystem()
    val javaFiles = fs.inputFiles(fs.predicates().hasLanguage("java"))

    val parser = JavaParser.createParser()

    javaFiles.asScala.toSeq
      .map { javaFile =>
        log.info(s"Parsing Java file: ${javaFile.toString}")
        (javaFile, parser.parse(javaFile.contents()))
      }
      .foreach {
        case (f, tree) =>
          Try(rules.foreach(_.scan(f, tree))) match {
            case Failure(ex) =>
              Loggers
                .get(classOf[SonarAcademicSensor])
                .error("Failed to scan file", ex)
            case _ =>
          }
      }

    rules.foreach(_.afterAllScanned(context))
  }
}
