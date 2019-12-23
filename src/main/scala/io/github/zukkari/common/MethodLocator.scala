package io.github.zukkari.common

import java.util

import io.github.zukkari.checks.Method
import org.sonar.java.ast.visitors.SubscriptionVisitor
import org.sonar.plugins.java.api.tree.{MethodTree, Tree}
import org.sonar.plugins.java.api.tree.Tree.Kind

import scala.jdk.CollectionConverters._

class MethodLocator(val filter: MethodTree => Boolean = _ => true) extends SubscriptionVisitor {
  override def nodesToVisit(): util.List[Tree.Kind] = List(Kind.METHOD).asJava

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
