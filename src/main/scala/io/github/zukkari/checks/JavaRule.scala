package io.github.zukkari.checks

import cats.effect.IO
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}
import org.sonar.plugins.java.api.tree.{BaseTreeVisitor, Tree}

trait JavaRule extends BaseTreeVisitor with JavaFileScanner {

  def reportIssue(issue: String, tree: Tree, condition: Boolean): Unit = {
    val expr = if (condition) {
      IO.pure(issue).map(msg => scannerContext.reportIssue(this, tree, msg))
    } else {
      IO(())
    }

    expr.unsafeRunAsyncAndForget()
  }

  def scannerContext: JavaFileScannerContext
}
