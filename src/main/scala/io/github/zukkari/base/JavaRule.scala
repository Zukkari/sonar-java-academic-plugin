package io.github.zukkari.base

import cats.effect.IO
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import org.sonar.api.config.Configuration
import org.sonar.java.ast.visitors.CognitiveComplexityVisitor
import org.sonar.plugins.java.api.tree.{BaseTreeVisitor, MethodTree, Tree}
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}

import scala.util.Try

trait ContextReporter {
  def report(issue: String, tree: Tree, condition: => Boolean): Unit = {
    val expr = if (condition) {
      IO.pure(issue).map(msg => scannerContext.reportIssue(check, tree, msg))
    } else {
      IO.unit
    }

    expr.unsafeRunSync()
  }

  def report(issue: String, line: Int, condition: => Boolean): Unit = {
    val expr = if (condition) {
      IO.pure(issue).map(msg => scannerContext.addIssue(line, check, msg))
    } else {
      IO.unit
    }

    expr.unsafeRunSync()
  }

  def report(issue: String, tree: Tree): Unit =
    report(issue, tree, condition = true)

  def scannerContext: JavaFileScannerContext

  def check: JavaFileScanner
}

trait JavaRule
    extends BaseTreeVisitor
    with JavaFileScanner
    with ContextReporter
    with ConfigurationAccessor {
  override def check: JavaFileScanner = this

  def safeOp[A](op: => A)(recover: A): A = Try(op).getOrElse(recover)
}

trait ComplexityAccessor {
  def complexity(methodTree: MethodTree): Int =
    CognitiveComplexityVisitor.methodComplexity(methodTree).complexity

  def complexity(iterable: Iterable[MethodTree]): Int =
    iterable.map(complexity).sum
}

trait ConfigurationAccessor {
  def config: Configuration = SonarAcademicRulesDefinition.configuration
}
