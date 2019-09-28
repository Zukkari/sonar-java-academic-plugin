package io.github.zukkari.config

import io.github.zukkari.config.RulesSyntax._
import org.mockito.MockitoSugar._
import org.scalatest.flatspec.AnyFlatSpec
import org.sonar.api.server.rule.RulesDefinition.NewRepository
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader

class RulesSyntaxSpec extends AnyFlatSpec {
  implicit val loader: RulesDefinitionAnnotationLoader = spy(mock[RulesDefinitionAnnotationLoader])
  implicit val repo: NewRepository = mock[NewRepository]

  it should "call loader with repository and check" in {
    val rules = Rules.get
    rules.foreach(_.makeRule)

    rules.foreach { rule =>
      verify(loader, atMost(1)).load(repo, rule)
    }
  }

  "Rule" should "be annotated with @Rule annotation" in {
    assert(Rules.get
      .map(_.annotation)
      .forall(_.nonEmpty)
    )
  }
}
