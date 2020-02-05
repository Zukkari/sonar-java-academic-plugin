package io.github.zukkari.checks

import io.github.zukkari.base.{ComplexityAccessor, JavaRule}
import io.github.zukkari.common.InstructionCounter
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.implicits._
import io.github.zukkari.syntax.ClassSyntax._
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.semantic.Type
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}

import scala.annotation.tailrec

@Rule(key = "LazyClass")
class LazyClass extends JavaRule with ComplexityAccessor {
  private var context: JavaFileScannerContext = _

  private var minNumberOfMethods: Int = _
  private var mediumNumberOfInstructions: Int = _
  private var lowComplexityMethodRatio: Double = _

  private var depthOfInheritance: Int = _
  private var couplingBetweenObjects: Int = _

  private var knownClasses = Set.empty[String]
  private var classAssociations = Map.empty[ClassTree, Set[String]]
  private var reported = Set.empty[String]

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext
  ): Unit = {
    minNumberOfMethods = config
      .flatMap(
        _.getInt(ConfigurationProperties.LAZY_CLASS_MIN_NUMBER_OF_METHODS.key)
      )
      .orElse(
        ConfigurationProperties.LAZY_CLASS_MIN_NUMBER_OF_METHODS.defaultValue.toInt
      )

    mediumNumberOfInstructions = config
      .flatMap(
        _.getInt(
          ConfigurationProperties.LAZY_CLASS_MEDIUM_NUMBER_OF_INSTRUCTIONS.key
        )
      )
      .orElse(
        ConfigurationProperties.LAZY_CLASS_MEDIUM_NUMBER_OF_INSTRUCTIONS.defaultValue.toInt
      )

    lowComplexityMethodRatio = config
      .flatMap(
        _.getDouble(
          ConfigurationProperties.LAZY_CLASS_LOW_COMPLEXITY_METHOD_RATIO.key
        )
      )
      .orElse(
        ConfigurationProperties.LAZY_CLASS_LOW_COMPLEXITY_METHOD_RATIO.defaultValue.toDouble
      )

    depthOfInheritance = config
      .flatMap(
        _.getInt(ConfigurationProperties.LAZY_CLASS_DEPTH_OF_INHERITANCE.key)
      )
      .orElse(
        ConfigurationProperties.LAZY_CLASS_DEPTH_OF_INHERITANCE.defaultValue.toInt
      )

    couplingBetweenObjects = config
      .flatMap(
        _.getInt(
          ConfigurationProperties.LAZY_CLASS_COUPLING_BETWEEN_OBJECTS.key
        )
      )
      .orElse(
        ConfigurationProperties.LAZY_CLASS_COUPLING_BETWEEN_OBJECTS.defaultValue.toInt
      )

    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    knownClasses += Option(tree.simpleName)
      .map(_.name)
      .map(_.toLowerCase)
      .getOrElse("")

    // Case 1: detect classes with low number of methods
    val methods = tree.methods
    report(
      s"Lazy class: number of methods is lower or equal to: $minNumberOfMethods",
      tree,
      methods.size <= minNumberOfMethods
    )

    // Case 2: detect methods with low number of instructions
    // and low complexity ratio
    val numOfInstructions =
      methods.foldRight(0)((method, acc) => acc + depth(method))

    val methodComplexity =
      methods.foldRight(0)((method, acc) => acc + complexity(method))
    val methodComplexityRatio = safeOp(methodComplexity / methods.size)(0)
    report(
      s"Lazy class: class contains low complexity methods",
      tree,
      methods.nonEmpty
        && numOfInstructions < mediumNumberOfInstructions
        && methodComplexityRatio <= lowComplexityMethodRatio
    )

    // Case 3: detect cases with low coupling and with high inheritance depth

    /* Idea:
      Calculate depth of hierarchy for classes and add
      potentially problematic classes to set.
      Collect another set of classes we know.
      Calculate the classes that this class is coupled with.
      For each new class visit, we check whether or not the
      amount of discovered classes is enough to report this class as a lazy class.
     */
    val hierarchyDepthValue = hierarchyDepth(tree)
    classAssociations = if (hierarchyDepthValue >= depthOfInheritance) {
      associates(tree) match {
        case associates if associates.nonEmpty =>
          classAssociations.updated(tree, associates)
        case _ => classAssociations
      }
    } else {
      classAssociations
    }

    // Check if any of the classes we have in the map have problems
    val (left, right) = classAssociations.partition(keyAndAssociates => {
      val coupling = keyAndAssociates._2.intersect(knownClasses).size
      coupling >= couplingBetweenObjects
    })

    classAssociations = right

    left
      .filter { case (key, _) => !reported.contains(key.simpleName.name) }
      .foreachEntry((key, _) => {
        report(
          s"Lazy class: depth of hierarchy is greater than $depthOfInheritance and coupling is higher than $couplingBetweenObjects",
          key
        )
      })
    reported ++= left.keySet.map(_.simpleName.name)

    super.visitClass(tree)
  }

  def depth(tree: MethodTree)(
      implicit ic: InstructionCounter[MethodTree]
  ): Int = ic.count(tree)

  def hierarchyDepth(c: ClassTree): Int = {
    @tailrec
    def _hierarchyDepth(c: Type, depth: Int): Int =
      c match {
        case t: Type => _hierarchyDepth(t.symbol.superClass, depth + 1)
        case _       => depth
      }

    _hierarchyDepth(c.symbol.superClass, 0)
  }

  def associates(tree: ClassTree): Set[String] =
    tree.variables
      .map(_.symbol.name)
      .toSet

}
