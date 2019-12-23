package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.common.MethodInvocationLocator
import io.github.zukkari.util.Log
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.MethodTree

@Rule(key = "DivergentChange")
class DivergentChange extends JavaRule {
  private val log = Log(classOf[DivergentChange])

  private val methodCallThreshold = 20

  private var context: JavaFileScannerContext = _

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitMethod(tree: MethodTree): Unit = {
    val locator = new MethodInvocationLocator

    val uniqueInvocations = locator.methodInvocations(tree)
    log.info(s"Total number of unique method invocations: ${uniqueInvocations.size} with total invocations ${locator.totalInvocations}")
    report("Divergent change", tree, locator.totalInvocations >= methodCallThreshold)

    super.visitMethod(tree)
  }
}
