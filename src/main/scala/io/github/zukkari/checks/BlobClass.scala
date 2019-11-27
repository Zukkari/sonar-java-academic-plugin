package io.github.zukkari.checks

import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree, VariableTree}
import org.sonar.plugins.java.api.tree.Tree.Kind

import scala.jdk.CollectionConverters._

@Rule(key = "BlobClass")
class BlobClass extends JavaRule {
  private var context: JavaFileScannerContext = _

  private val numberOfVariables = 13
  private val numberOfMethods = 22

  private val lackOfCohesion = 40

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitClass(tree: ClassTree): Unit = {
    // Find variables of the class
    val variables = tree.members.asScala
      .filter(_.is(Kind.VARIABLE))
      .map(_.asInstanceOf[VariableTree])

    if (variables.size < numberOfVariables) {
      // Number of variables lower than threshold
      // so no point in going further
      return
    }

    // Find methods of the class
    val methods = tree.members.asScala
      .filter(_.is(Kind.METHOD))
      .map(_.asInstanceOf[MethodTree])

    if (methods.size < numberOfMethods) {
      // Number of methods is lower than threshold
      // no point in going further
      return
    }

    // Calculate cohesion between variables


    super.visitClass(tree)
  }
}
