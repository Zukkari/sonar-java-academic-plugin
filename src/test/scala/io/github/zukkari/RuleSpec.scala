package io.github.zukkari

import java.io.File

import org.sonar.java.checks.verifier.JavaCheckVerifier
import org.sonar.plugins.java.api.JavaFileScanner

import scala.jdk.CollectionConverters._

trait RuleSpec extends BaseSpec {
  def verifyRule(scanner: JavaFileScanner, check: String): Unit = {

    JavaCheckVerifier.verify(
      s"src/test/resources/files/$dir/$check.java",
      scanner,
      List(new File("target/scala-2.13/test-classes")).asJavaCollection)
  }

  def dir: String
}
