package io.github.zukkari.checks

import io.github.zukkari.BaseSpec
import org.sonar.java.checks.verifier.JavaCheckVerifier
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.ClassTree

class DataClassRuleSpec extends BaseSpec {
  val context: JavaFileScannerContext = mock[JavaFileScannerContext]
  val classTree: ClassTree = mock[ClassTree]

  val rule = new DataClassRule

  it should "detect simple data class with getters and setters" in {
    JavaCheckVerifier.verify("src/test/resources/files/DataClassWithGetters.java", new DataClassRule())
  }

  it should "detect data class without getters and setters and public fields" in {
    JavaCheckVerifier.verify("src/test/resources/files/DataClass.java", new DataClassRule())
  }

}
