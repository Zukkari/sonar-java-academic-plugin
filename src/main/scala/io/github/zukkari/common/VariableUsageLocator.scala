package io.github.zukkari.common

import java.util
import java.util.Collections

import org.sonar.java.ast.visitors.SubscriptionVisitor
import org.sonar.plugins.java.api.tree.{IdentifierTree, MemberSelectExpressionTree, Tree}
import org.sonar.plugins.java.api.tree.Tree.Kind

class VariableUsageLocator extends SubscriptionVisitor {

  override def nodesToVisit(): util.List[Tree.Kind] = Collections.singletonList(Kind.MEMBER_SELECT)

  private var variableState = Set.empty[String]

  override def visitNode(tree: Tree): Unit = {
    val memberSelect = tree.asInstanceOf[MemberSelectExpressionTree]

    variableState = Option(memberSelect.expression)
      .filter(_.isInstanceOf[IdentifierTree])
      .map(_.asInstanceOf[IdentifierTree])
      .map(_.name) match {
      case Some(variable) => variableState + variable
      case None => variableState
    }

    super.visitNode(tree)
  }

  def variables(tree: Tree): Set[String] = {
    scanTree(tree)
    variableState
  }
}
