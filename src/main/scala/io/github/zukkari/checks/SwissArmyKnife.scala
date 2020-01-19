package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext

@Rule(key = "SwissArmyKnife")
class SwissArmyKnife(
    private val veryHighNumberOfMethods: Int
) extends JavaRule {

  def this() = this(veryHighNumberOfMethods = 13)

  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }
}
