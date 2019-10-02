package io.github.zukkari.checks

import cats.effect.SyncIO
import org.sonar.api.Property
import org.sonar.check.Rule
import org.sonar.java.resolve.JavaSymbol.{MethodJavaSymbol, TypeJavaSymbol, VariableJavaSymbol}
import org.sonar.java.resolve.{ClassJavaType, JavaSymbol}
import org.sonar.plugins.java.api.tree._
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}

import scala.jdk.CollectionConverters._

case class Traversal(methodName: String = "", depth: Int)

@Rule(key = "MessageChainRule")
class MessageChainRule extends BaseTreeVisitor with JavaFileScanner {

  @Property(key = "sonar.android.plugin.message.chain.length", name = "Maximum length of message chain", defaultValue = "2")
  val chainLength: Int = 2

  private var context: JavaFileScannerContext = _

  override def scanFile(context: JavaFileScannerContext): Unit = {
    this.context = context

    scan(context.getTree)
  }

  override def visitMethodInvocation(tree: MethodInvocationTree): Unit = {
    val methodDepth = depth(tree)

    val report = if (methodDepth > chainLength) {
      SyncIO.pure(s"Message chain length is $methodDepth. Reduce chain length to at least: $chainLength")
        .map(m => context.reportIssue(this, tree, m))
    } else {
      SyncIO(())
    }

    report.unsafeRunSync()
  }

  private def nextMethodName(javaSymbol: MethodJavaSymbol): String =
    Option(javaSymbol.declaration).map(_.simpleName.name).getOrElse("")


  def depth(tree: MethodInvocationTree): Int = depth(tree, Traversal(depth = 1))

  def depth(tree: MethodInvocationTree, traversal: Traversal): Int = {
    tree.symbol match {
      case javaSymbol: MethodJavaSymbol =>
        val methodName = nextMethodName(javaSymbol)
        depthSymbolTree(javaSymbol, Traversal(methodName, traversal.depth))
      case _ =>
        traversal.depth
    }
  }

  def depthSymbolTree(symbol: JavaSymbol.MethodJavaSymbol, traversal: Traversal): Int = {
    val block = Option(symbol.declaration).map(_.block.body)

    val blockDepth = for {
      b <- block
    } yield (b.size, b.get(0).isInstanceOf[ReturnStatementTree]) match {
      case (1, true) => depthExpression(b.get(0).asInstanceOf[ReturnStatementTree], traversal)
      case _ => traversal.depth
    }

    blockDepth match {
      case Some(value) => value
      case None => traversal.depth
    }
  }

  def depthExpression(tree: ReturnStatementTree, traversal: Traversal): Int = {
    tree.expression match {
      case invocationTree: MethodInvocationTree =>
        depthMethodInvocationTree(invocationTree, traversal)
      case _ =>
        traversal.depth
    }
  }

  def depthMethodInvocationTree(tree: MethodInvocationTree, traversal: Traversal): Int = {
    tree.methodSelect match {
      case expressionTree: MemberSelectExpressionTree =>
        depthMemberSelectExpression(expressionTree, traversal)
      case identifierTree: IdentifierTree if identifierTree.symbol.isInstanceOf[MethodJavaSymbol] =>
        val javaSymbol = identifierTree.symbol.asInstanceOf[MethodJavaSymbol]
        val methodName = nextMethodName(javaSymbol)
        depthSymbolTree(javaSymbol, Traversal(methodName, traversal.depth + 1))
      case _ =>
        traversal.depth
    }
  }

  def depthMemberSelectExpression(tree: MemberSelectExpressionTree, traversal: Traversal): Int = {
    tree.expression match {
      case identifierTree: IdentifierTree =>
        depthIdentifier(identifierTree, Traversal(tree.identifier.name, traversal.depth))
      case _ =>
        traversal.depth
    }
  }

  def depthIdentifier(tree: IdentifierTree, traversal: Traversal): Int = {
    tree.symbol match {
      case variable: VariableJavaSymbol =>
        depthVariableSymbol(variable, traversal)
      case _ => traversal.depth
    }
  }

  def depthVariableSymbol(symbol: VariableJavaSymbol, traversal: Traversal): Int = {
    symbol.getType match {
      case javaType: ClassJavaType =>
        depthType(javaType.getSymbol, traversal)
      case _ =>
        traversal.depth
    }
  }

  def depthType(symbol: TypeJavaSymbol, traversal: Traversal): Int = {
    val nextInvocationTree = symbol.declaration.members.asScala.toList
      .filter(method => method.isInstanceOf[MethodTree]
        && method.asInstanceOf[MethodTree].simpleName.name() == traversal.methodName)
      .map(_.asInstanceOf[MethodTree].block.body)
      .filter(_.size == 1)
      .map(_.get(0))
      .filter(_.isInstanceOf[ReturnStatementTree])
      .map(st => st.asInstanceOf[ReturnStatementTree].expression)
      .filter(_.isInstanceOf[MethodInvocationTree])
      .map(tree => tree.asInstanceOf[MethodInvocationTree])

    if (nextInvocationTree.nonEmpty) {
      1 + traversal.depth + depth(nextInvocationTree.head)
    } else {
      traversal.depth
    }
  }
}
