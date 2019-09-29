package io.github.zukkari.checks

import io.github.zukkari.BaseSpec
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.ClassTree

class DataClassRuleSpec extends BaseSpec {
  val context: JavaFileScannerContext = mock[JavaFileScannerContext]
  val classTree: ClassTree = mock[ClassTree]

  val rule = new DataClassRule

  it should "throw NotImplementedError when scanning file" in {
    assertThrows[NotImplementedError] {
      rule.scanFile(context)
    }
  }

  it should "throw NotImplementedError when visiting class" in {
    assertThrows[NotImplementedError] {
      rule.visitClass(classTree)
    }
  }

}
