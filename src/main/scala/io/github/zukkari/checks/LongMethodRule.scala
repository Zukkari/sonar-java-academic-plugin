package io.github.zukkari.checks

import io.github.zukkari.common.InstructionCounter
import io.github.zukkari.implicits._
import org.sonar.api.Property
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree._

@Rule(key = "LongMethodRule")
class LongMethodRule extends JavaRule {

  @Property(key = "sonar.academic.plugin.long.method.length", name = "Maximum number of statements / expressions in a method", defaultValue = "8")
  val methodLength: Int = 8

  private var context: JavaFileScannerContext = _

  override def scanFile(context: JavaFileScannerContext): Unit = {
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

  def count(tree: MethodTree)(implicit ic: InstructionCounter[MethodTree]): Int = ic.count(tree)
}
