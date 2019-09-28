package io.github.zukkari.checks

import org.sonar.check.Rule
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}
import org.sonar.plugins.java.api.tree.{BaseTreeVisitor, ClassTree}

@Rule(key = "DataClassRule")
class DataClassRule extends BaseTreeVisitor with JavaFileScanner {

  override def scanFile(context: JavaFileScannerContext): Unit = ???

  override def visitClass(tree: ClassTree): Unit = ???

}
