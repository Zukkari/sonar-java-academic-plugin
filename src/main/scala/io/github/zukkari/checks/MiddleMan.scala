package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.checks.MethodSyntax._
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.ClassSyntax._
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{
  ClassTree,
  MethodInvocationTree,
  MethodTree,
  ReturnStatementTree
}

@Rule(key = "MiddleMan")
class MiddleMan extends JavaRule {
  private var context: JavaFileScannerContext = _

  private var delegationRatio: Double = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
    javaFileScannerContext: JavaFileScannerContext
  ): Unit = {

    delegationRatio = config
      .flatMap(
        _.getDouble(ConfigurationProperties.MIDDLE_MAN_DELEGATE_RATIO.key)
      )
      .orElse(
        ConfigurationProperties.MIDDLE_MAN_DELEGATE_RATIO.defaultValue.toDouble
      )

    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    val methods = tree.methods

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
