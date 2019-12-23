package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.SwitchStatementTree

@Rule(key = "SwitchStatementRule")
class SwitchStatementRule extends JavaRule {
  private var context: JavaFileScannerContext = _

  override def scanFile(context: JavaFileScannerContext): Unit = {
    this.context = context

    scan(context.getTree)
  }

  override def visitSwitchStatement(tree: SwitchStatementTree): Unit = {
    report("Usage of switch statements is prohibited", tree)
  }

  override def scannerContext: JavaFileScannerContext = context
}
