package io.github.zukkari.checks

import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext

@Rule(key ="ShotgunSurgery")
class ShotgunSurgeryRule extends JavaRule {
  private var context: JavaFileScannerContext = _

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(javaFileScannerContext.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

}
