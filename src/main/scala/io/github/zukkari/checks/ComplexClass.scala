package io.github.zukkari.checks

import io.github.zukkari.base.{ComplexityAccessor, ContextReporter, JavaRule}
import io.github.zukkari.config.ConfigurationProperties
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}
import org.sonar.plugins.java.api.tree.Tree.Kind

import scala.jdk.CollectionConverters._

@Rule(key = "ComplexClass")
class ComplexClass
    extends JavaRule
    with ComplexityAccessor
    with ContextReporter {

  private var context: JavaFileScannerContext = _

  private var veryHighClassComplexity: Double = _

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    veryHighClassComplexity = config
      .flatMap(
        _.getDouble(
          ConfigurationProperties.COMPLEX_CLASS_VERY_HIGH_COMPLEXITY.key))
      .orElse(
        ConfigurationProperties.COMPLEX_CLASS_VERY_HIGH_COMPLEXITY.defaultValue.toDouble)

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitClass(tree: ClassTree): Unit = {
    val classComplexity = tree.members.asScala
      .filter(_.is(Kind.METHOD))
      .map(_.asInstanceOf[MethodTree])
      .map(complexity)
      .sum

    report(
      s"Complex class: class complexity $classComplexity is higher than configured: $veryHighClassComplexity",
      tree,
      classComplexity >= veryHighClassComplexity
    )

    super.visitClass(tree)
  }
}
