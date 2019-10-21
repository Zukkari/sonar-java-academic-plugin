package io.github.zukkari.checks

import cats.effect.IO
import org.sonar.plugins.java.api.tree.{BaseTreeVisitor, Tree}
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}

trait ContextReporter {
  def report(issue: String, tree: Tree, condition: Boolean): Unit = {
    val expr = if (condition) {
      IO.pure(issue).map(msg => scannerContext.reportIssue(check, tree, msg))
    } else {
      IO(())
    }

    expr.unsafeRunAsyncAndForget()
  }

  def report(issue: String, tree: Tree): Unit = report(issue, tree, condition = true)

  def scannerContext: JavaFileScannerContext

  def check: JavaFileScanner
}

trait JavaRule extends BaseTreeVisitor with JavaFileScanner with ContextReporter {
  override def check: JavaFileScanner = this
}
