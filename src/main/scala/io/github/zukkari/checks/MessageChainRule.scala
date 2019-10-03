package io.github.zukkari.checks

import cats.Monoid
import cats.effect.SyncIO
import cats.implicits._
import io.github.zukkari.implicits._
import org.sonar.api.Property
import org.sonar.check.Rule
import org.sonar.java.resolve.JavaSymbol.{MethodJavaSymbol, TypeJavaSymbol, VariableJavaSymbol}
import org.sonar.java.resolve.{ClassJavaType, JavaSymbol}
import org.sonar.plugins.java.api.tree._
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}

import scala.jdk.CollectionConverters._

case class Traversal(methodName: String, depth: Int) {
  def +(that: Traversal)(implicit m: Monoid[String]): Traversal = Traversal(m.empty, depth + that.depth)
}

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
    val methodDepth = depth(tree).depth

    val report = if (methodDepth > chainLength) {
      SyncIO.pure(s"Message chain length is $methodDepth. Reduce chain length to at least: $chainLength")
        .map(m => context.reportIssue(this, tree, m))
    } else {
      SyncIO(())
    }

    report.unsafeRunSync()
  }

  private def nextMethodName(javaSymbol: MethodJavaSymbol)(implicit m: Monoid[String]): String =
    Option(javaSymbol.declaration).map(_.simpleName.name).getOrElse(m.empty)


  def depth(tree: MethodInvocationTree)
           (implicit m: Monoid[Traversal]): Traversal = depth(tree, m.empty)

  def depth(tree: MethodInvocationTree, traversal: Traversal): Traversal = {
    tree.symbol match {
      case javaSymbol: MethodJavaSymbol =>
        val methodName = nextMethodName(javaSymbol)
        depthSymbolTree(javaSymbol, Traversal(methodName, traversal.depth))
      case _ =>
        traversal
    }
  }

  def depthSymbolTree(symbol: JavaSymbol.MethodJavaSymbol, traversal: Traversal): Traversal = {
    val block = Option(symbol.declaration).map(_.block.body)

    val blockDepth = for {
      b <- block
    } yield (b.size, b.get(0)) match {
      case (1, returnStatement: ReturnStatementTree) => depthExpression(returnStatement, traversal)
      case _ => traversal
    }

    blockDepth match {
      case Some(value) => value
      case None => traversal
    }
  }

  def depthExpression(tree: ReturnStatementTree, traversal: Traversal): Traversal = {
    tree.expression match {
      case invocationTree: MethodInvocationTree =>
        depthMethodInvocationTree(invocationTree, traversal)
      case _ =>
        traversal
    }
  }

  def depthMethodInvocationTree(tree: MethodInvocationTree, traversal: Traversal): Traversal = {
    tree.methodSelect match {
      case expressionTree: MemberSelectExpressionTree =>
        depthMemberSelectExpression(expressionTree, traversal)
      case identifierTree: IdentifierTree if identifierTree.symbol.isInstanceOf[MethodJavaSymbol] =>
        val javaSymbol = identifierTree.symbol.asInstanceOf[MethodJavaSymbol]
        val methodName = nextMethodName(javaSymbol)
        depthSymbolTree(javaSymbol, Traversal(methodName, traversal.depth + 1))
      case _ =>
        traversal
    }
  }

  def depthMemberSelectExpression(tree: MemberSelectExpressionTree, traversal: Traversal): Traversal = {
    tree.expression match {
      case identifierTree: IdentifierTree =>
        depthIdentifier(identifierTree, Traversal(tree.identifier.name, traversal.depth + 1))
      case _ =>
        traversal
    }
  }

  def depthIdentifier(tree: IdentifierTree, traversal: Traversal): Traversal = {
    tree.symbol match {
      case variable: VariableJavaSymbol =>
        depthVariableSymbol(variable, traversal)
      case _ => traversal
    }
  }

  def depthVariableSymbol(symbol: VariableJavaSymbol, traversal: Traversal): Traversal = {
    symbol.getType match {
      case javaType: ClassJavaType =>
        depthType(javaType.getSymbol, traversal)
      case _ =>
        traversal
    }
  }

  def depthType(symbol: TypeJavaSymbol, traversal: Traversal): Traversal = {
    val nextInvocationTree = symbol.declaration.members.asScala.toList
      .filter(method => method.isInstanceOf[MethodTree]
        && method.asInstanceOf[MethodTree].simpleName.name() == traversal.methodName)
      .map(_.asInstanceOf[MethodTree].block.body)
      .filter(_.size == 1)
      .map(_.get(0))
      .filter(_.isInstanceOf[ReturnStatementTree])
      .map(_.asInstanceOf[ReturnStatementTree].expression)
      .filter(_.isInstanceOf[MethodInvocationTree])
      .map(_.asInstanceOf[MethodInvocationTree])

    if (nextInvocationTree.nonEmpty) {
      Monoid[Traversal].empty + traversal + depth(nextInvocationTree.head)
    } else {
      traversal
    }
  }
}
