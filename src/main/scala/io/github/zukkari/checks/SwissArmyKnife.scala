package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.ClassTree
import org.sonar.plugins.java.api.tree.Tree.Kind

import io.github.zukkari.syntax.ClassSyntax._

@Rule(key = "SwissArmyKnife")
class SwissArmyKnife(
    private val veryHighNumberOfMethods: Int
) extends JavaRule {

  def this() = this(veryHighNumberOfMethods = 13)

  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    val tooHighNumberOfMethods = Option(tree)
      .filter(_.is(Kind.INTERFACE))
      .exists(_.methods.size >= veryHighNumberOfMethods)

    report(
      s"Swiss army knife: number of methods in interface higher than threshold '$veryHighNumberOfMethods'",
      tree,
      tooHighNumberOfMethods
    )

    super.visitClass(tree)
  }
}
