package io.github.zukkari.checks
import org.sonar.plugins.java.api.JavaFileScannerContext

class LongParameterList extends JavaRule {
  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }
}
