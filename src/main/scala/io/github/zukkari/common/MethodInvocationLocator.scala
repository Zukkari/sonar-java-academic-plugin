package io.github.zukkari.common

import io.github.zukkari.checks.Method
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{MethodInvocationTree, Tree}

class MethodInvocationLocator extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.METHOD_INVOCATION)

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
