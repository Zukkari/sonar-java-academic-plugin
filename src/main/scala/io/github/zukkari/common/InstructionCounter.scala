package io.github.zukkari.common

import org.sonar.plugins.java.api.tree._

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

trait InstructionCounter[A] {
  def count(a: A): Int
}

object InstructionCounterInstances {
  val methodInstructionCounter: InstructionCounter[MethodTree] = new InstructionCounter[MethodTree] {
    override def count(tree: MethodTree): Int = depth(tree.block)

    private def depth(tree: StatementTree): Int = {
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
}
