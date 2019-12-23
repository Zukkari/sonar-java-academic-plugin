package io.github.zukkari.visitor

import java.util

import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{SyntaxToken, SyntaxTrivia, Tree}

import scala.jdk.CollectionConverters._

trait SonarAcademicSubscriptionVisitor {
  private var _nodesToVisit: List[Tree.Kind] = Nil
  private var visitToken: Boolean = false
  private var visitTrivia: Boolean = false

  def nodesToVisit: List[Tree.Kind]

  def visitNode(tree: Tree): Unit = {
  }

  def leaveNode(tree: Tree): Unit = {
  }

  def visitToken(syntaxToken: SyntaxToken): Unit = {
  }

  def visitTrivia(syntaxTrivia: SyntaxTrivia): Unit = {
  }

  final def scanTree(tree: Tree): Unit = {
    if (_nodesToVisit == Nil) {
      _nodesToVisit = nodesToVisit
    }

    visitToken = isVisitingTokens
    visitTrivia = isVisitingTrivia
    visit(tree)
  }

  final def visit(tree: Tree): Unit = {
    val subscribed = isSubscribed(tree)
    val shouldVisitSyntaxToken = (visitToken || visitTrivia) && tree.is(Kind.TOKEN)

    if (shouldVisitSyntaxToken) {
      val syntaxToken = tree.asInstanceOf[SyntaxToken]
      if (visitToken) {
        visitToken(syntaxToken)
      }

      if (visitTrivia) {
        syntaxToken.trivias.forEach(visitTrivia(_))
      }
    } else if (subscribed) {
      visitNode(tree)
    }

    visitChildren(tree)
    if (!shouldVisitSyntaxToken && subscribed) {
      leaveNode(tree)
    }
  }

  final def visitChildren(tree: Tree): Unit = {
    val isLeaf = tree.getClass.getMethod("isLeaf")
    if (!isLeaf.invoke(tree).asInstanceOf[Boolean]) {
      val children = tree.getClass.getMethod("getChildren")
      children.invoke(tree).asInstanceOf[util.List[Tree]]
        .asScala
        .filter(_ != null)
        .foreach(visit)
    }
  }

  private def isSubscribed(tree: Tree): Boolean = nodesToVisit contains tree.kind()

  private def isVisitingTrivia: Boolean = nodesToVisit contains Kind.TRIVIA

  private def isVisitingTokens: Boolean = nodesToVisit contains Kind.TOKEN
}
