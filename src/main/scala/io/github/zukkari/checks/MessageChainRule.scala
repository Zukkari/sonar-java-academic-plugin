package io.github.zukkari.checks

import cats.Monoid
import cats.implicits._
import io.github.zukkari.base.JavaRule
import io.github.zukkari.checks.ChainSyntax._
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.implicits._
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree._

import scala.annotation.tailrec

case class Chain(depth: Int)

@Rule(key = "MessageChainRule")
class MessageChainRule extends JavaRule {

  private var chainLength: Int = _

  private var context: JavaFileScannerContext = _

  override def scanFile(context: JavaFileScannerContext): Unit = {
    chainLength = config
      .getInt(ConfigurationProperties.MESSAGE_CHAIN_LENGTH.key)
      .orElse(3)

    this.context = context

    scan(context.getTree)
  }

  override def visitMethodInvocation(tree: MethodInvocationTree): Unit = {
    val methodDepth = depth(tree).depth

    report(
      s"Message chain length is $methodDepth. Reduce chain length to at least: $chainLength",
      tree,
      methodDepth > chainLength
    )
  }

  def depth(tree: MethodInvocationTree)(implicit m: Monoid[Chain]): Chain =
    depth(tree, m.empty)

  def depth(tree: MethodInvocationTree, chain: Chain): Chain = {
    @tailrec
    def depth1(f: () => ExpressionTree, chain: Chain): Chain = {
      f() match {
        case memberSelect: MemberSelectExpressionTree =>
          depth1(memberSelect.expression, chain)
        case methodInvocation: MethodInvocationTree =>
          depth1(methodInvocation.methodSelect, chain.increment)
        case _ => chain
      }
    }

    depth1(tree.methodSelect, chain)
  }

  override def scannerContext: JavaFileScannerContext = context
}

object ChainSyntax {
  implicit class ChainOps(chain: Chain) {
    def increment: Chain = Monoid[Chain].empty |+| chain
  }
}
