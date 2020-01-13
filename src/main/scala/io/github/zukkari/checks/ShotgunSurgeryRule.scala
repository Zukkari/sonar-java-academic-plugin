package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.{MethodInvocationTree, MethodTree}

import scala.jdk.CollectionConverters._

case class Method(methodName: String,
                  returnType: String,
                  parameters: List[String])

object Method {
  def apply(tree: MethodInvocationTree): Method = {
    Method(
      tree.symbol.name,
      Option(tree.symbolType).map(_.symbol).map(_.name).getOrElse(""),
      tree.arguments.asScala.toList.map(arg =>
        Option(arg.symbolType).map(_.fullyQualifiedName).getOrElse(""))
    )
  }

  def apply(tree: MethodTree): Method = {
    Method(
      tree.simpleName.name,
      Option(tree.returnType.symbolType)
        .map(_.fullyQualifiedName)
        .getOrElse(""),
      tree.parameters.asScala.toList.map(
        varTree =>
          Option(varTree.`type`)
            .map(_.symbolType)
            .map(_.fullyQualifiedName)
            .getOrElse(""))
    )
  }
}

/** Basic idea is that we will store state in the Map
  * State in this case is method declaration tree to number of times the method is invoked
  * For this we will visit all method declarations and invocations
  * For each declaration, we add it to map with initial count 0
  * For each invocation, we check whether or not the method
  * is defined in our Map.
  * If it is, we increment the invocation counter.
  * If counter > allowed, we report an issue to method declaration context.
  * Else, we add method invocation to a list, and when new declaration is added
  * we check existing pending method invocations whether or not they were declared by the user. */

@Rule(key = "ShotgunSurgery")
class ShotgunSurgeryRule extends JavaRule {
  private val issueThreshold = 3

  private var context: JavaFileScannerContext = _

  private var contextMap = Map.empty[Method, MethodTree]
  private var methodMap = Map.empty[Method, Int]
  private var delayedInvocation: List[Method] = Nil

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(javaFileScannerContext.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitMethod(tree: MethodTree): Unit = {
    val method = Method(tree)

    methodMap += (method -> 0)
    contextMap += (method -> tree)

    // After first round check all previous invocations
    val (added, existing) = delayedInvocation.partition(methodMap.contains)
    methodMap = added.foldRight(methodMap)((invocation, map) =>
      map.updatedWith(invocation)(_.map(_ + 1)))
    delayedInvocation = existing

    // Check for issues after incrementing the counters
    checkForIssues()

    super.visitMethod(tree)
  }

  override def visitMethodInvocation(tree: MethodInvocationTree): Unit = {
    val invocation = Method(tree)

    if (methodMap contains invocation) {
      methodMap = methodMap.updatedWith(invocation)(_.map(_ + 1))
    } else {
      delayedInvocation ::= invocation
    }

    checkForIssues()
  }

  private def checkForIssues(): Unit = {
    methodMap.foreach {
      case (method, count) =>
        contextMap.get(method) match {
          case Some(m) =>
            report("Shotgun surgery detected", m, count >= issueThreshold)
          case _ => ()
        }
    }
  }
}
