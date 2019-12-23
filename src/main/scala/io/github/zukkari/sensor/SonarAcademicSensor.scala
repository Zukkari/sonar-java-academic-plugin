package io.github.zukkari.sensor

import io.github.zukkari.base.SensorRule
import io.github.zukkari.checks.{CyclicDependenciesRule, TraditionBreakerRule}
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import io.github.zukkari.util.Log
import org.sonar.api.batch.sensor.{Sensor, SensorContext, SensorDescriptor}
import org.sonar.java.ast.parser.JavaParser

import scala.jdk.CollectionConverters._

class SonarAcademicSensor extends Sensor {
  private val log = Log(this.getClass)

  override def describe(descriptor: SensorDescriptor): Unit = {
    descriptor.name("Sensor Sonar Academic Plugin")
    descriptor.onlyOnLanguage("java")
    descriptor.createIssuesForRuleRepository(SonarAcademicRulesDefinition.repoKey)
  }

  override def execute(context: SensorContext): Unit = {
    log.info("Executing cyclic dependencies check")

    val fs = context.fileSystem()
    val javaFiles = fs.inputFiles(fs.predicates().hasLanguage("java"))

    val parser = JavaParser.createParser()

    val checks: List[SensorRule] = List(
      new CyclicDependenciesRule,
      new TraditionBreakerRule
    )

    javaFiles.asScala
      .toSeq
      .map { javaFile =>
        log.info(s"Parsing Java file: ${javaFile.toString}")
        (javaFile, parser.parse(javaFile.contents()))
      }
      .foreach {
        case (f, tree) => checks.foreach(_.scan(f, tree))
      }

    checks.foreach(_.afterAllScanned(context))
  }
}