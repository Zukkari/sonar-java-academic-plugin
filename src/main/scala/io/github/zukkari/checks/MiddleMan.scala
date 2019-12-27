package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{
  ClassTree,
  MethodInvocationTree,
  MethodTree,
  ReturnStatementTree
}
import MethodSyntax._
import org.sonar.plugins.java.api.tree.Tree.Kind

import scala.jdk.CollectionConverters._

@Rule(key = "MiddleMan")
class MiddleMan extends JavaRule {
  private var context: JavaFileScannerContext = _

  private val delegationRatio = 0.5

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    val methods = tree.members.asScala.toList
      .filter(_.is(Kind.METHOD))
      .map(_.asInstanceOf[MethodTree])

    val totalMethods = methods.size
    val delegateCount = methods.count(_.isDelegate)

    val ratio = delegateCount / totalMethods.toDouble
    report(
      s"Middle man: delegation ratio is ${BigDecimal(ratio).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble} with limit set to $delegationRatio",
      tree,
      ratio > delegationRatio
    )

    super.visitClass(tree)
  }
}

object MethodSyntax {
  implicit class MethodOps(val methodTree: MethodTree) {
    def isDelegate: Boolean = {
      Option(methodTree.block)
        .map(_.body)
        .filter(!_.isEmpty)
        .map(_.get(0))
        .filter(_.is(Kind.RETURN_STATEMENT))
        .map(_.asInstanceOf[ReturnStatementTree])
        .map(_.expression)
        .filter(_.is(Kind.METHOD_INVOCATION))
        .map(_.asInstanceOf[MethodInvocationTree])
        .map(_.methodSelect)
        .exists(_.is(Kind.MEMBER_SELECT))
    }
  }
}
