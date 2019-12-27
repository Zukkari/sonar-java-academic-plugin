package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext

@Rule(key = "MiddleMan")
class MiddleMan extends JavaRule {
  private var context: JavaFileScannerContext = _

  private val delegationRatio = 0.5

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }
}
