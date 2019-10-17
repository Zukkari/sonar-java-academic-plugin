package io.github.zukkari

import org.sonar.java.checks.verifier.JavaCheckVerifier
import org.sonar.plugins.java.api.JavaFileScanner

trait RuleSpec extends BaseSpec {
  def verifyRule(scanner: JavaFileScanner, check: String): Unit = {
    JavaCheckVerifier.verify(s"src/test/resources/files/$dir/$check.java", scanner)
  }

  def dir: String
}
