package io.github.zukkari.common

import io.github.zukkari.checks.Method
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{MethodTree, Tree}

class MethodLocator(val filter: MethodTree => Boolean = _ => true)
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.METHOD)

  private var methods = Set.empty[Method]

  override def visitNode(tree: Tree): Unit = {
    val method = tree.asInstanceOf[MethodTree]

    methods = if (filter(method)) methods + Method(method) else methods

    super.visitNode(tree)
  }

  def methods(tree: Tree): Set[Method] = {
    scanTree(tree)
    methods
  }
}
