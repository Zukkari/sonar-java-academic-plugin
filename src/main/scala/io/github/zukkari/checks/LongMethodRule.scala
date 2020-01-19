package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.common.InstructionCounter
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.implicits._
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree._

@Rule(key = "LongMethodRule")
class LongMethodRule extends JavaRule {

  private var methodLength: Int = _

  private var context: JavaFileScannerContext = _

  override def scanFile(context: JavaFileScannerContext): Unit = {
    methodLength = config
      .getInt(ConfigurationProperties.LONG_METHOD_METHOD_LENGTH.key)
      .orElse(26)

    this.context = context

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitMethod(tree: MethodTree): Unit = {
    val expressions = count(tree)

    report(
      s"Reduce length of this method to at least $methodLength",
      tree,
      expressions > methodLength
    )
  }

  def count(tree: MethodTree)(
      implicit ic: InstructionCounter[MethodTree]): Int = ic.count(tree)
}
