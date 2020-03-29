package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{ClassTree, SyntaxTrivia, Tree}
@Rule(key = "CommentDetectionRule")
class CommentDetectionRule extends JavaRule {
  private var context: JavaFileScannerContext = _

  private var veryHighNumberOfComments: Double = _

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext
  ): Unit = {
    context = javaFileScannerContext

    veryHighNumberOfComments = config
      .flatMap(_.getDouble(ConfigurationProperties.COMMENT_DETECTION_RULE.key))
      .orElse(
        ConfigurationProperties.COMMENT_DETECTION_RULE.defaultValue.toDouble)

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitClass(tree: ClassTree): Unit = {
    val visitor = new CommentDetectionVisitor
    visitor.scanTree(tree)

    report(
      s"This class contains too many comments ${visitor.count} versus configured $veryHighNumberOfComments",
      tree,
      visitor.count >= veryHighNumberOfComments)

    super.visitClass(tree)
  }
}

class CommentDetectionVisitor extends SonarAcademicSubscriptionVisitor {
  var count = 0

  override def nodesToVisit: List[Tree.Kind] = List(Kind.TRIVIA)

  override def visitTrivia(syntaxToken: SyntaxTrivia): Unit = {
    if (!syntaxToken.isJavadocComment && !syntaxToken.testFrameworkComment) {
      count += 1
    }

    super.visitTrivia(syntaxToken)
  }

  implicit class SyntaxTriviaOps(val syntaxTrivia: SyntaxTrivia) {
    def isJavadocComment: Boolean = syntaxTrivia.comment.startsWith("/**")

    def testFrameworkComment: Boolean =
      syntaxTrivia.comment.startsWith("// Noncompliant")
  }
}
