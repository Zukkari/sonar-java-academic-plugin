package io.github.zukkari.checks

import cats.effect.IO
import cats.implicits._
import io.github.zukkari.base.SensorRule
import io.github.zukkari.syntax.ClassSyntax._
import io.github.zukkari.syntax.SymbolSyntax._
import io.github.zukkari.syntax.VariableSyntax._
import io.github.zukkari.util.Log
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{ClassTree, Tree}
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.immutable.Graph

@Rule(key = "CyclicDependencies")
class CyclicDependenciesRule extends JavaCheck with SensorRule {
  private val log = Log(this.getClass)

  private var fileMapContext: Map[String, InputFile] = Map.empty
  private var classDeclarationContext: Map[String, Int] = Map.empty
  private var classDependenciesContext: Map[String, Set[String]] = Map.empty

  override def scan(t: Tree): Unit = {
    val visitor = new ClassDependenciesVisitor

    // Fully classified class name to dependencies
    val declarations = visitor.declarations(t)
    val dependencies = visitor.dependencies(t)

    fileMapContext ++= declarations.map { case (a, _) => (a, inputFile) }
    classDependenciesContext ++= dependencies
    classDeclarationContext ++= declarations
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    val nodes = classDependenciesContext.keySet
    val edges = for {
      (k, v) <- classDependenciesContext
      dep <- v
    } yield DiEdge(k, dep)

    val graph = Graph.from(nodes, edges)

    nodes.foreach { node =>
      (
        graph.findCycleContaining(graph get node),
        classDeclarationContext.get(node),
        fileMapContext.get(node)
      ) match {
        case (Some(cycle), Some(line), Some(javaFile)) =>
          IO {
            report(
              sensorContext,
              s"Cycle detected: $cycle",
              Declaration(javaFile, line),
              CyclicDependenciesRule.ruleKey
            )
          }.unsafeRunSync()
          log.info(s"Cycle detected: $cycle")
        case _ =>
      }
    }
  }
}

object CyclicDependenciesRule {
  val ruleKey = "CyclicDependencies"
}

class ClassDependenciesVisitor extends SonarAcademicSubscriptionVisitor {
  private val log = Log(this.getClass)

  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS)

  // Fully qualified class names of dependencies
  private var dependencies = Map.empty[String, Set[String]]

  private var declarationMap = Map.empty[String, Int]

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]

    val parent = classTree.symbol().fullyQualifiedName
    parent match {
      case None => super.visitNode(tree)
      case Some(p) =>
        log.info(s"Visiting class: $p")

        // To remember where to report the issue
        declarationMap += p -> classTree.firstToken.line

        // Get dependencies here
        val deps = classTree.variables
          .map(_.variableType)
          .toList
          .traverse(identity)
          .map(_.toSet)
          .getOrElse(Set.empty)

        log.info(s"Class $p has following dependencies: $deps")
        dependencies += p -> deps

        super.visitNode(tree)
    }
  }

  def dependencies(tree: Tree): Map[String, Set[String]] = {
    dependencies
  }

  def declarations(tree: Tree): Map[String, Int] = {
    scanTree(tree)
    declarationMap
  }
}
