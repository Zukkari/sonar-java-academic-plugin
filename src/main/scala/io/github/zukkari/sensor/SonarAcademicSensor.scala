package io.github.zukkari.sensor

import io.github.zukkari.base.SensorRule
import io.github.zukkari.checks._
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import io.github.zukkari.sonar.java.ast.parser.JavaParser
import io.github.zukkari.sonar.java.model.{JavaVersionImpl, VisitorsBridge}
import io.github.zukkari.sonar.java.se.SymbolicExecutionMode
import io.github.zukkari.util.Log
import org.sonar.api.batch.sensor.{Sensor, SensorContext, SensorDescriptor}
import org.sonar.api.utils.log.Loggers
import org.sonar.plugins.java.api.JavaCheck

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Try}

class SonarAcademicSensor extends Sensor {
  private val log = Log(this.getClass)

  private val unstableDependencies: UnstableDependencies =
    new UnstableDependencies

  var rules: List[SensorRule] = List(
    new CyclicDependenciesRule,
    new TraditionBreakerRule,
    new DataClump,
    new ParallelInheritanceHierarchies,
    new SpeculativeGeneralityInterfaces,
    new PrimitiveObsession,
    new BrainMethod,
    new InappropriateIntimacy,
    new AlternativeClassesWithDifferentInterfaces,
    unstableDependencies, {
      val stableAbstractionBreaker = new StableAbstractionBreaker
      stableAbstractionBreaker.unstableDependencies = unstableDependencies
      stableAbstractionBreaker
    }
  )

  override def describe(descriptor: SensorDescriptor): Unit = {
    descriptor.name("Sensor Sonar Academic Plugin")
    descriptor.onlyOnLanguage("java")
    descriptor.createIssuesForRuleRepository(
      SonarAcademicRulesDefinition.repoKey)
  }

  override def execute(context: SensorContext): Unit = {
    log.info(s"Configuring ${rules.size} rules using ${context.config()}")
    rules.foreach(_.configure(context.config()))
    log.info("Finished rule configuration")

    val javaClassPath =
      new SonarJavaClasspath(context.config(), context.fileSystem())
    javaClassPath.init()

    val fs = context.fileSystem()
    val javaFiles = fs.inputFiles(fs.predicates().hasLanguage("java"))

    val parser = JavaParser.createParser()

    val classPath = javaClassPath.getElements

    val visitor = new VisitorsBridge(
      rules.map(_.asInstanceOf[JavaCheck]).asJavaCollection,
      classPath,
      null,
      SymbolicExecutionMode.DISABLED)

    visitor.setJavaVersion(JavaVersionImpl.fromString("8"))

    javaFiles.asScala.toSeq
      .map { javaFile =>
        log.info(s"Parsing Java file: ${javaFile.toString}")
        val tree = parser.parse(javaFile.contents())

        (javaFile, tree)
      }
      .foreach {
        case (f, tree) =>
          Try({
            visitor.setCurrentFile(f)
            rules.foreach(_.inputFile = f)
            visitor.visitFile(tree)
            rules.foreach(_.scan(tree))
          }) match {
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
