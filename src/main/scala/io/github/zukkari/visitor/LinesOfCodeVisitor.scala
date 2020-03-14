package io.github.zukkari.visitor

import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{SyntaxToken, Tree}

class LinesOfCodeVisitor extends SonarAcademicSubscriptionVisitor {
  private var lines: Set[Int] = Set.empty

  override def nodesToVisit: List[Tree.Kind] = List(Kind.TOKEN)

  def linesOfCode(tree: Tree): Int = {
    lines = Set.empty
    scanTree(tree)
    lines.size
  }

  override def visitToken(syntaxToken: SyntaxToken): Unit = {
    val isEOF = syntaxToken.getClass
      .getMethod("isEOF")
      .invoke(syntaxToken)
      .asInstanceOf[Boolean]
    if (!isEOF) {
      lines += syntaxToken.line()
    }
  }
}
