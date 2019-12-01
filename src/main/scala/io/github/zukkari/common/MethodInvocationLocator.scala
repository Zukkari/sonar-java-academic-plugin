package io.github.zukkari.common

import java.util

import io.github.zukkari.checks.Method
import org.sonar.java.ast.visitors.SubscriptionVisitor
import org.sonar.plugins.java.api.tree.{MethodInvocationTree, Tree}
import org.sonar.plugins.java.api.tree.Tree.Kind

import scala.jdk.CollectionConverters._

class MethodInvocationLocator extends SubscriptionVisitor {
  override def nodesToVisit(): util.List[Tree.Kind] = List(Kind.METHOD_INVOCATION).asJava

  private var methodInvocations = Set.empty[Method]

  override def visitNode(tree: Tree): Unit = {
    val invocation = tree.asInstanceOf[MethodInvocationTree]

    methodInvocations += Method(invocation)

    super.visitNode(tree)
  }

  def methodInvocations(tree: Tree): Set[Method] = {
    scanTree(tree)
    methodInvocations
  }
}
