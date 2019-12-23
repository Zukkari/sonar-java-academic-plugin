package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext

@Rule(key = "DivergentChange")
class DivergentChange extends JavaRule {
  private var context: JavaFileScannerContext = _

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context
}
