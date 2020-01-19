package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.config.ConfigurationProperties
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.ClassTree
import org.sonar.plugins.java.api.tree.Tree.Kind
import io.github.zukkari.syntax.ClassSyntax._

@Rule(key = "SwissArmyKnife")
class SwissArmyKnife extends JavaRule {

  private var veryHighNumberOfMethods: Int = _

  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    veryHighNumberOfMethods = config
      .getInt(
        ConfigurationProperties.SWISS_ARMY_KNIFE_HIGH_NUMBER_OF_METHODS.key)
      .orElse(13)

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
