package io.github.zukkari.checks

import org.sonar.api.Property
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree._

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

@Rule(key = "LongMethodRule")
class LongMethodRule(
                      @Property(key = "sonar.academic.plugin.long.method.length", name = "Maximum number of statements / expressions in a method", defaultValue = "8")
                      val methodLength: Int = 8
                    ) extends JavaRule {

  private var context: JavaFileScannerContext = _

  override def scanFile(context: JavaFileScannerContext): Unit = {
    this.context = context

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitMethod(tree: MethodTree): Unit = {
    val expressions = countExpressions(tree)

    reportIssue(
      s"Reduce length of this method to at least $methodLength",
      tree,
      expressions > methodLength
    )
  }

  def countExpressions(tree: MethodTree): Int = depth(tree.block)

  def depth(tree: StatementTree): Int = {
    @tailrec
    def _depth(trees: List[StatementTree], acc: Int): Int = {
      trees match {
        case x :: xs =>
          val (toVisit, depth) = blockDepth(x)
          _depth(toVisit ++ xs, depth + acc)
        case Nil => acc
      }
    }

    def blockDepth(tree: StatementTree): (List[StatementTree], Int) = {
      tree match {
        case block: BlockTree =>
          (Nil, block.body.asScala.map(depth).sum)
        case doWhile: DoWhileStatementTree =>
          (List(doWhile.statement), 1)
        case forTree: ForStatementTree =>
          (List(forTree.statement), 1)
        case synchronized: SynchronizedStatementTree =>
          (List(synchronized.block), 1)
        case tryTree: TryStatementTree =>
          (List(tryTree.block, tryTree.finallyBlock) ++ tryTree.catches.asScala.map(_.block), 3)
        case whileTree: WhileStatementTree =>
          (List(whileTree.statement), 1)
        case ifTree: IfStatementTree =>
          (List(ifTree.thenStatement, ifTree.elseStatement), 2)
        case _ => (Nil, 1)
      }
    }

    _depth(List(tree), 0)
  }
}
