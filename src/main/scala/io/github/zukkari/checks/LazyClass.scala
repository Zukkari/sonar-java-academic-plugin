package io.github.zukkari.checks
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}

import scala.jdk.CollectionConverters._

@Rule(key = "LazyClassRule")
class LazyClass extends JavaRule {
  private var context: JavaFileScannerContext = _

  private val minNumberOfMethods = 0

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    // Case 1: detect classes with low number of methods
    val methods = tree.members.asScala.filter(_.isInstanceOf[MethodTree])
    report(s"Lazy class: number of methods is lower or equal to: ${minNumberOfMethods}", tree, methods.size <= minNumberOfMethods)

    super.visitClass(tree)
  }
}
