package io.github.zukkari.checks
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.MethodTree

@Rule(key = "LongParameterRule")
class LongParameterList extends JavaRule {
  private val threshold = 9

  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitMethod(tree: MethodTree): Unit = {
    report("Long parameter list", tree, tree.parameters.size >= threshold)

    super.visitMethod(tree)
  }
}
