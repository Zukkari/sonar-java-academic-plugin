package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.ClassSyntax._
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.ClassTree
import org.sonar.plugins.java.api.tree.Tree.Kind

@Rule(key = "SwissArmyKnife")
class SwissArmyKnife extends JavaRule {

  private var veryHighNumberOfMethods: Double = _

  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext
  ): Unit = {
    veryHighNumberOfMethods = config
      .flatMap(
        _.getDouble(
          ConfigurationProperties.SWISS_ARMY_KNIFE_HIGH_NUMBER_OF_METHODS.key
        )
      )
      .orElse(
        ConfigurationProperties.SWISS_ARMY_KNIFE_HIGH_NUMBER_OF_METHODS.defaultValue.toDouble
      )

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
