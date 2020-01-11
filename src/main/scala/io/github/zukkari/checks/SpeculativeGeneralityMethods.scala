package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.MethodTree

import scala.jdk.CollectionConverters._

@Rule(key = "SpeculativeGeneralityMethods")
class SpeculativeGeneralityMethods extends JavaRule {
  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitMethod(tree: MethodTree): Unit = {
    val args = tree.parameters.asScala
      .map { varTree =>
        Option(varTree.simpleName)
      }
      .filter(_.isDefined)
      .map(_.get)
      .toList

    val visitor = new IdentifierVisitor
    visitor.scanTree(tree.block)

    val usedParameters = visitor.identifiers

    args.foreach(
      arg =>
        report(
          s"Speculative generality: unused method parameter: '${arg.toString}'",
          arg,
          !usedParameters.contains(arg.toString)))

    super.visitMethod(tree)
  }
}
