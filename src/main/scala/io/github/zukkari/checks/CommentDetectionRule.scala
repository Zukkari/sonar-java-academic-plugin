package io.github.zukkari.checks

import java.util

import io.github.zukkari.base.ContextReporter
import org.sonar.check.Rule
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{SyntaxTrivia, Tree}
import org.sonar.plugins.java.api.{IssuableSubscriptionVisitor, JavaFileScanner, JavaFileScannerContext}

import scala.jdk.CollectionConverters._

@Rule(key = "CommentDetectionRule")
class CommentDetectionRule extends IssuableSubscriptionVisitor with ContextReporter {

  override def nodesToVisit(): util.List[Tree.Kind] = List(Kind.TRIVIA).asJava

  override def visitTrivia(syntaxTrivia: SyntaxTrivia): Unit = {
    report("Comments should not be used", syntaxTrivia.startLine, !syntaxTrivia.isJavadocComment && !syntaxTrivia.testFrameworkComment)

    super.visitTrivia(syntaxTrivia)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def check: JavaFileScanner = this

  implicit class SyntaxTriviaOps(val syntaxTrivia: SyntaxTrivia) {
    def isJavadocComment: Boolean = syntaxTrivia.comment.startsWith("/**")

    def testFrameworkComment: Boolean = syntaxTrivia.comment.startsWith("// Noncompliant")
  }

}
