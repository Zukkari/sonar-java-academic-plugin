package io.github.zukkari.checks

import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.MethodTree

@Rule(key ="ShotgunSurgery")
class ShotgunSurgeryRule extends JavaRule {
  private var context: JavaFileScannerContext = _

  override def scanFile(javaFileScannerContext: JavaFileScannerContext): Unit = {
    this.context = javaFileScannerContext

    scan(javaFileScannerContext.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitMethod(tree: MethodTree): Unit = {
    // Basic idea is that we will store state in the Map
    // State in this case is method declaration tree to number of times the method is invoked
    // For this we will visit all method declarations and invocations
    // For each declaration, we add it to map with initial count 0
    // For each invocation, we check whether or not the method
    // is defined in our Map.
    // If it, we increment the invocation counter.
    // If counter > allowed, we report an issue to method declaration context.
    // Else, we add method invocation to a list, and when new declaration is added
    // we check existing pending method invocations whether or not they were declared by the user.
  }
}
