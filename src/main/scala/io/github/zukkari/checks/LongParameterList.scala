package io.github.zukkari.checks
import io.github.zukkari.base.JavaRule
import io.github.zukkari.config.ConfigurationProperties
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.MethodTree

@Rule(key = "LongParameterList")
class LongParameterList extends JavaRule {
  private var parameterCount: Double = _

  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext
  ): Unit = {
    parameterCount = config
      .flatMap(
        _.getDouble(
          ConfigurationProperties.LONG_PARAMETER_LIST_PARAMETER_COUNT.key
        )
      )
      .orElse(
        ConfigurationProperties.LONG_PARAMETER_LIST_PARAMETER_COUNT.defaultValue.toDouble
      )

    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitMethod(tree: MethodTree): Unit = {
    report("Long parameter list", tree, tree.parameters.size >= parameterCount)

    super.visitMethod(tree)
  }
}
