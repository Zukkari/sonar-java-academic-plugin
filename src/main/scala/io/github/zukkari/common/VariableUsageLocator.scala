package io.github.zukkari.common

import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{IdentifierTree, MemberSelectExpressionTree, Tree}

class VariableUsageLocator extends SonarAcademicSubscriptionVisitor {

  override def nodesToVisit: List[Tree.Kind] = List(Kind.MEMBER_SELECT)

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
