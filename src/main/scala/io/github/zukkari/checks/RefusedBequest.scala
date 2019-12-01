package io.github.zukkari.checks

import io.github.zukkari.common.{MethodInvocationLocator, MethodLocator}
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.ClassTree

@Rule(key = "RefusedBequestRule")
class RefusedBequest extends JavaRule {
  private var context: JavaFileScannerContext = _

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
        val classInvocations = classMap.getOrElse(c, Set.empty)
        val parentMethods = classMap.getOrElse(className, Set.empty)

        classToTreeMap.get(c).foreach(classTree =>
          report("Refused bequest: class does not use parents protected members", classTree, !classInvocations.exists(parentMethods.contains))
        )
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
      val classMethods = methodInvocations(tree)
      val parentClassMethods = classMap.getOrElse(parentClass, Set.empty)

      report("Refused bequest: class does not use parents protected members", tree, !classMethods.exists(parentClassMethods.contains))
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
}
