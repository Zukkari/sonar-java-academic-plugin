package io.github.zukkari.checks

import io.github.zukkari.common.{MethodInvocationLocator, MethodLocator}
import org.sonar.check.Rule
import org.sonar.java.ast.visitors.CognitiveComplexityVisitor
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree}

import scala.jdk.CollectionConverters._

@Rule(key = "RefusedBequest")
class RefusedBequest extends JavaRule {
  private var context: JavaFileScannerContext = _

  private val numberOfProtectedMethods = 3
  private val baseClassUsageRatio = 1.0 / 3.0
  private val baseClassOverrideRatio = 1.0 / 3.0
  private val averageMethodWeight = 2.0
  private val weightedMethodCount = 14
  private val numberOfMethods = 7

  private var classMap = Map.empty[String, Set[Method]]
  private var pendingClasses = Map.empty[String, List[String]]
  private var classToTreeMap = Map.empty[String, ClassTree]

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    // Find protected members of this class
    val className = tree.simpleName.name
    classMap += (className -> methods(tree))
    classToTreeMap += (className -> tree)

    if (pendingClasses contains className) {
      // Found the class that other classes were waiting for
      val pending = pendingClasses.getOrElse(className, Set.empty)
      pending.foreach(c => {
        countComplexityAndReport(className, c)
      })
    }

    // Two possibilities
    // 1. we already scanned parent class and found its parents methods
    // then we can check all method invocations inside the class and see
    // if the class is using its parents protected members

    // 2. we still dont know anything about parent class
    // we have to delay scanning until we find parent class
    val parentClass = tree.symbol.superClass.name
    if (classMap contains parentClass) {
      // We can check usage of the methods
      countComplexityAndReport(parentClass, className)
    } else {
      // Delay till we know something about the parent
      pendingClasses = pendingClasses.updatedWith(parentClass) {
        case Some(existing) => Some(className :: existing)
        case None => Some(className :: Nil)
      }
    }

    super.visitClass(tree)
  }

  def methodInvocations(tree: ClassTree): Set[Method] =
    new MethodInvocationLocator()
      .methodInvocations(tree)

  def methods(tree: ClassTree): Set[Method] = {
    new MethodLocator(_.symbol.isProtected)
      .methods(tree)
  }

  def overriddenProtectedMembers(tree: ClassTree): Set[Method] = {
    new MethodLocator(m => m.symbol.isProtected && m.isOverriding)
      .methods(tree)
  }

  def complexity(method: MethodTree): Int = CognitiveComplexityVisitor.methodComplexity(method).complexity

  private def countComplexityAndReport(parentClassName: String, thisClassName: String): Unit = {
    val classInvocations = classMap.getOrElse(thisClassName, Set.empty)
    val parentMethods = classMap.getOrElse(parentClassName, Set.empty)

    val parentProtectedNumber = parentMethods.size
    val baseClassUsageRatio = safeOp(classInvocations.intersect(parentMethods).size / parentProtectedNumber)(0)

    val maybeClassTree = classToTreeMap.get(thisClassName)
    for {
      classTree <- maybeClassTree
    } {
      val classMethods = classTree.members.asScala.filter(_.isInstanceOf[MethodTree]).map(_.asInstanceOf[MethodTree]).toList
      val baseClassOverrideRatio = safeOp(overriddenProtectedMembers(classTree).size / classMethods.size.doubleValue)(0)
      val averageMethodWeight = classMethods.map(complexity).sum / classMethods.size.doubleValue

      report("Refused bequest: class does not use parents protected members", classTree,
        parentProtectedNumber > numberOfProtectedMethods &&
          baseClassUsageRatio < this.baseClassUsageRatio ||
          baseClassOverrideRatio < this.baseClassOverrideRatio &&
            ((averageMethodWeight > this.averageMethodWeight || weightedMethodCount > this.weightedMethodCount)
              && classMethods.size > numberOfMethods)
      )
    }
  }
}
