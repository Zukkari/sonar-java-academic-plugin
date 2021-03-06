package io.github.zukkari.checks

import java.util.UUID

import io.github.zukkari.base.{ComplexityAccessor, JavaRule}
import io.github.zukkari.common.{MethodInvocationLocator, MethodLocator}
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.ClassSyntax._
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.ClassTree
import io.github.zukkari.syntax.SymbolSyntax._
@Rule(key = "RefusedBequest")
class RefusedBequest extends JavaRule with ComplexityAccessor {
  private var context: JavaFileScannerContext = _

  private var numberOfProtectedMethods: Int = _
  private var baseClassUsageRatio: Double = _
  private var baseClassOverrideRatio: Double = _
  private var averageMethodWeight: Double = _
  private var weightedMethodCount: Int = _
  private var numberOfMethods: Int = _

  private var classMap = Map.empty[String, Set[Method]]
  private var pendingClasses = Map.empty[String, List[String]]
  private var classToTreeMap = Map.empty[String, ClassTree]

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext
  ): Unit = {
    numberOfProtectedMethods = config
      .flatMap(
        _.getInt(
          ConfigurationProperties.REFUSED_BEQUEST_NUMBER_OF_PROTECTED_METHODS.key
        )
      )
      .orElse(
        ConfigurationProperties.REFUSED_BEQUEST_NUMBER_OF_PROTECTED_METHODS.defaultValue.toInt
      )

    baseClassUsageRatio = config
      .flatMap(
        _.getDouble(
          ConfigurationProperties.REFUSED_BEQUEST_BASE_CLASS_USAGE_RATIO.key
        )
      )
      .orElse(
        ConfigurationProperties.REFUSED_BEQUEST_BASE_CLASS_USAGE_RATIO.defaultValue.toDouble
      )

    baseClassOverrideRatio = config
      .flatMap(
        _.getDouble(
          ConfigurationProperties.REFUSED_BEQUEST_BASE_CLASS_OVERRIDE_RATIO.key
        )
      )
      .orElse(
        ConfigurationProperties.REFUSED_BEQUEST_BASE_CLASS_OVERRIDE_RATIO.defaultValue.toDouble
      )

    averageMethodWeight = config
      .flatMap(
        _.getDouble(
          ConfigurationProperties.REFUSED_BEQUEST_AVERAGE_METHOD_WEIGHT.key
        )
      )
      .orElse(
        ConfigurationProperties.REFUSED_BEQUEST_AVERAGE_METHOD_WEIGHT.defaultValue.toDouble
      )

    weightedMethodCount = config
      .flatMap(
        _.getInt(
          ConfigurationProperties.REFUSED_BEQUEST_WEIGHTED_METHOD_COUNT.key
        )
      )
      .orElse(
        ConfigurationProperties.REFUSED_BEQUEST_WEIGHTED_METHOD_COUNT.defaultValue.toInt
      )

    numberOfMethods = config
      .flatMap(
        _.getInt(
          ConfigurationProperties.REFUSED_BEQUEST_NUMBER_OF_PROTECTED_METHODS.key
        )
      )
      .orElse(
        ConfigurationProperties.REFUSED_BEQUEST_NUMBER_OF_PROTECTED_METHODS.defaultValue.toInt
      )

    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    // Find protected members of this class
    val className =
      tree.symbol().fullyQualifiedName.getOrElse(UUID.randomUUID.toString)
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
    val parentClass = Option(tree.symbol())
      .map(_.superClass())
      .map(_.symbol())
      .flatMap(_.fullyQualifiedName)
      .getOrElse(UUID.randomUUID().toString)
    if (classMap contains parentClass) {
      // We can check usage of the methods
      countComplexityAndReport(parentClass, className)
    } else {
      // Delay till we know something about the parent
      pendingClasses = pendingClasses.updatedWith(parentClass) {
        case Some(existing) => Some(className :: existing)
        case None           => Some(className :: Nil)
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

  private def countComplexityAndReport(parentClassName: String,
                                       thisClassName: String): Unit = {
    val classInvocations =
      classToTreeMap.get(thisClassName).map(methodInvocations)
    val parentMethods = classMap.getOrElse(parentClassName, Set.empty)

    val parentProtectedNumber = parentMethods.size

    val maybeClassTree = classToTreeMap.get(thisClassName)
    for {
      classTree <- maybeClassTree
      invocations <- classInvocations
    } {
      val baseClassUsageRatio = safeOp(
        invocations
          .intersect(parentMethods)
          .size / parentProtectedNumber.doubleValue
      )(0)
      val methods = classTree.methods
      val baseClassOverrideRatio = safeOp(
        overriddenProtectedMembers(classTree).size / parentMethods.size.doubleValue
      )(0)
      val weightedMethodCount = complexity(methods)
      val averageMethodWeight = weightedMethodCount / methods.size.doubleValue

      report(
        "Refused bequest: class does not use parents protected members",
        classTree,
        parentProtectedNumber > numberOfProtectedMethods &&
          baseClassUsageRatio < this.baseClassUsageRatio ||
          baseClassOverrideRatio < this.baseClassOverrideRatio &&
            ((averageMethodWeight > this.averageMethodWeight || weightedMethodCount > this.weightedMethodCount)
              && methods.size > numberOfMethods)
      )
    }
  }
}
