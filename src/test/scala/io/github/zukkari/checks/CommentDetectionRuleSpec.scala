package io.github.zukkari.checks

import io.github.zukkari.RuleSpec

class CommentDetectionRuleSpec extends RuleSpec {
  val check = new CommentDetectionRule

  it should "detect comments in files except for Javadoc" in {
    verifyRule(check, "Comments")
  }

  override def dir: String = "comments"
}
