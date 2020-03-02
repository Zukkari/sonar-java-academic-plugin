package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.ClassTree
import org.sonar.plugins.java.api.tree.Tree.Kind
import io.circe.generic.auto._
import io.circe.syntax._
import scala.jdk.CollectionConverters._

case class InterfaceStatistics(numOfMethods: Int)

@Rule(key = "InterfaceStatsCollector")
class InterfaceStatsCollector extends JavaRule {
  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    if (tree.is(Kind.INTERFACE)) {
      val numOfMethods = tree.members().asScala.count(_.is(Kind.METHOD))

      report(InterfaceStatistic(numOfMethods).asJson.toString(), tree)
    }

    super.visitClass(tree)
  }
}
