package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{MethodInvocationTree, MethodTree, Tree}

@Rule(key = "IntensiveCoupling")
class IntensiveCoupling(
    private val calledMethodCount: Int,
    private val halfCouplingDispersion: Double,
    private val quarterCouplingDispersion: Double,
    private val couplingIntensity: Int,
    private val nestingDepth: Int
) extends JavaRule {

  def this() = this(
    calledMethodCount = 7,
    halfCouplingDispersion = 0.5,
    quarterCouplingDispersion = 0.25,
    couplingIntensity = 2,
    nestingDepth = 1
  )

  private var context: JavaFileScannerContext = _

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitMethod(tree: MethodTree): Unit = {
    val visitor = new IntensiveCouplingVisitor
    visitor.visit(tree)

    val methodCount = visitor.methods.size

    val nestingVisitor = new NestingVisitor
    nestingVisitor.visit(tree)

    val isHighNumberOfMethodsCalled =
      methodCount >= calledMethodCount &&
        visitor.classes.size / methodCount
          .max(1)
          .toDouble <= halfCouplingDispersion

    val isHighCoupling = methodCount >= couplingIntensity &&
      visitor.classes.size / methodCount
        .max(1)
        .toDouble <= quarterCouplingDispersion

    report(
      if (isHighNumberOfMethodsCalled)
        "Intensive coupling: method count is higher than short term memory count"
      else
        "Intensive coupling: too high coupling with external methods",
      tree,
      (isHighNumberOfMethodsCalled || isHighCoupling) &&
        nestingVisitor.maxNestingLevel >= nestingDepth
    )

    super.visitMethod(tree)
  }
}

class IntensiveCouplingVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.METHOD_INVOCATION)

  var classes: Set[String] = Set.empty
  var methods: Set[String] = Set.empty

  override def visitNode(tree: Tree): Unit = {
    val invocation = tree.asInstanceOf[MethodInvocationTree]

    val maybeOwner = Option(invocation.symbol).map(_.owner).map(_.name)
    classes = maybeOwner match {
      case Some(owner) => classes + owner
      case None        => classes
    }

    methods = (Option(invocation.symbol).map(_.name), maybeOwner) match {
      case (Some(value), Some(owner)) => methods + s"$owner#$value"
      case _                          => methods
    }

    super.visitNode(tree)
  }
}
