package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class BlobClassSpec extends RuleSpec {
  val rule = new BlobClass

  override def dir: String = "blob_class"

  it should "detect blob classes" in {
    verifyRule(rule, "BlobClass")
  }
}
