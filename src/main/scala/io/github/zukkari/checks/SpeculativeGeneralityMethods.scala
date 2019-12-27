package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{IdentifierTree, MethodTree, Tree}

import scala.jdk.CollectionConverters._

@Rule(key = "SpeculativeGeneralityMethods")
class SpeculativeGeneralityMethods extends JavaRule {
  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitMethod(tree: MethodTree): Unit = {
    val args = tree.parameters
      .asScala
      .map { varTree =>
        Option(varTree.simpleName)
      }
      .filter(_.isDefined)
      .map(_.get)
      .toList


    val visitor = new IdentifierVisitor
    visitor.scanTree(tree.block)

    val usedParameters = visitor.identifiers

    args.foreach(arg => report(s"Speculative generality: unused method parameter: '${arg.toString}'", arg, !usedParameters.contains(arg.toString)))

    super.visitMethod(tree)
  }
}

class IdentifierVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.IDENTIFIER)

  var identifiers: Set[String] = Set.empty

  override def visitNode(tree: Tree): Unit = {
    val identifier = tree.asInstanceOf[IdentifierTree]

    identifiers += identifier.toString

    super.visitNode(tree)
  }
}
