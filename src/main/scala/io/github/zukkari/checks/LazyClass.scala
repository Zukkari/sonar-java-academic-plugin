package io.github.zukkari.checks

import io.github.zukkari.common.InstructionCounter
import io.github.zukkari.implicits._
import org.sonar.check.Rule
import org.sonar.java.ast.visitors.CognitiveComplexityVisitor
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}

import scala.jdk.CollectionConverters._
import scala.util.Try

@Rule(key = "LazyClassRule")
class LazyClass extends JavaRule {
  private var context: JavaFileScannerContext = _

  private val minNumberOfMethods = 0
  private val mediumNumberOfInstructions = 50
  private val lowComplexityMethodRatio = 2

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    // Case 1: detect classes with low number of methods
    val methods: List[MethodTree] = tree.members.asScala.filter(_.isInstanceOf[MethodTree]).map(_.asInstanceOf[MethodTree]).toList
    report(s"Lazy class: number of methods is lower or equal to: $minNumberOfMethods", tree, methods.size <= minNumberOfMethods)

    // Case 2: detect methods with low number of instructions
    // and low complexity ratio
    val numOfInstructions = methods.foldRight(0)((method, acc) => acc + depth(method))

    val methodComplexity = methods.foldRight(0)((method, acc) => acc + complexity(method))
    val methodComplexityRatio = safeOp(methodComplexity / methods.size)(0)
    report(s"Lazy class: class contains low complexity methods", tree,
      methods.nonEmpty
        && numOfInstructions < mediumNumberOfInstructions
        && methodComplexityRatio <= lowComplexityMethodRatio)

    super.visitClass(tree)
  }

  def depth(tree: MethodTree)(implicit ic: InstructionCounter[MethodTree]): Int = ic.count(tree)

  def complexity(tree: MethodTree): Int = CognitiveComplexityVisitor.methodComplexity(tree).complexity

  def safeOp[A](op: => A)(recover: A): A = Try(op).getOrElse(recover)
}
