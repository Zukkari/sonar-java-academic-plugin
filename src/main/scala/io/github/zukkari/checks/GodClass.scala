package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext

@Rule(key = "GodClass")
class GodClass extends JavaRule {
  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit =
    ???

  override def scannerContext: JavaFileScannerContext = ???
}
