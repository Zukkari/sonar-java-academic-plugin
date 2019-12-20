package io.github.zukkari.sensor

import java.util

import io.github.zukkari.checks.CyclicDependenciesRule
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import io.github.zukkari.util.Log
import org.sonar.api.batch.sensor.{Sensor, SensorContext, SensorDescriptor}
import org.sonar.api.rule.RuleKey
import org.sonar.java.ast.parser.JavaParser
import org.sonar.java.ast.visitors.SubscriptionVisitor
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{ClassTree, IdentifierTree, Tree, VariableTree}
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.immutable.Graph

import scala.jdk.CollectionConverters._

class CyclicDependencies extends Sensor {
  private val log = Log(this.getClass)

  override def describe(descriptor: SensorDescriptor): Unit = {
    descriptor.name("Scan classes for cyclic dependencies")
    descriptor.onlyOnLanguage("java")
    descriptor.createIssuesForRuleRepository(SonarAcademicRulesDefinition.repoKey)
  }

  override def execute(context: SensorContext): Unit = {
    log.info("Executing cyclic dependencies check")

    val fs = context.fileSystem()
    val javaFiles = fs.inputFiles(fs.predicates().hasLanguage("java"))

    val parser = JavaParser.createParser()

    val declarationsToDependencies = javaFiles.asScala
      .toSeq
      .map { javaFile =>
        log.info(s"Parsing Java file: ${javaFile.toString}")
        (javaFile, parser.parse(javaFile.contents()))
      }
      .map { case (f, tree) =>
        val visitor = new ClassDependenciesVisitor

        // Fully classified class name to dependencies
        val declarations = visitor.declarations(tree)
        val dependencies = visitor.dependencies(tree)
        (declarations.map { case (a, _) => (a, f) }, declarations, dependencies)
      }

    val classToDependencies = declarationsToDependencies.map { case (_, _, c) => c }.reduce(_ ++ _)
    val declarationLines = declarationsToDependencies.map { case (_, b, _) => b }.reduce(_ ++ _)
    val classToFile = declarationsToDependencies.map { case (a, _, _) => a }.reduce(_ ++ _)

    val nodes = classToDependencies.keySet
    val edges = for {
      (k, v) <- classToDependencies
      dep <- v
    } yield DiEdge(k, dep)

    val graph = Graph.from(nodes, edges)

    nodes.foreach { node =>
      (graph.findCycleContaining(graph get node), declarationLines.get(node), classToFile.get(node)) match {
        case (Some(cycle), Some(line), Some(javaFile)) =>
          val newIssue = context.newIssue
            .forRule(RuleKey.of(SonarAcademicRulesDefinition.repoKey, CyclicDependenciesRule.ruleKey))

          val location = newIssue.newLocation()
            .on(javaFile)
            .at(javaFile.selectLine(line))
            .message(s"Cycle detected: $cycle")

          newIssue.at(location)
          newIssue.save()

          log.info(s"Cycle detected: $cycle")

        case _ =>
      }
    }
  }
}

class ClassDependenciesVisitor extends SubscriptionVisitor {
  private val log = Log(this.getClass)

  override def nodesToVisit(): util.List[Tree.Kind] = List(Kind.CLASS).asJava

  // Fully qualified class names of dependencies
  private var dependencies = Map.empty[String, Set[String]]

  private var declarationMap = Map.empty[String, Int]

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]

    val parent = classTree.simpleName.name
    log.info(s"Visiting class: $parent")

    // To remember where to report the issue
    declarationMap += parent -> classTree.firstToken.line

    // Get dependencies here
    val deps = classTree.members
      .asScala
      .toSeq
      .filter(_.is(Kind.VARIABLE))
      .map(_.asInstanceOf[VariableTree])
      .map(_.`type`)
      .filter(_.isInstanceOf[IdentifierTree])
      .map(_.asInstanceOf[IdentifierTree])
      .map(_.name)
      .toSet

    log.info(s"Class $parent has following dependencies: $deps")
    dependencies += parent -> deps

    super.visitNode(tree)
  }

  def dependencies(tree: Tree): Map[String, Set[String]] = {
    dependencies
  }

  def declarations(tree: Tree): Map[String, Int] = {
    scanTree(tree)
    declarationMap
  }
}
