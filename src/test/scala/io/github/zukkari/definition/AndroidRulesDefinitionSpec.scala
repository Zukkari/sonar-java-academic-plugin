package io.github.zukkari.definition

import io.github.zukkari.config.Rules.JavaCheckClass
import io.github.zukkari.config.{Language, Rules}
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar._
import org.scalatest.flatspec.AnyFlatSpec
import org.sonar.api.server.rule.RulesDefinition
import org.sonar.api.server.rule.RulesDefinition.{NewRepository, NewRule}
import org.sonar.check.Rule
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}

class AndroidRulesDefinitionSpec extends AnyFlatSpec with ArgumentMatchersSugar {
  val context: RulesDefinition.Context = spy(mock[RulesDefinition.Context])
  implicit val repo: NewRepository = mock[NewRepository]

  val definition = new AndroidRulesDefinition

  it should "call create repository for context" in {
    when(context.createRepository(definition.repoKey, Language.Java)).thenAnswer(repo)
    when(repo.setName(any)).thenAnswer(repo)
    when(repo.createRule(any)).thenAnswer(mock[NewRule])

    definition.define(context)

    verify(context, atLeastOnce).createRepository(definition.repoKey, Language.Java)
  }

  it should "create annotated rules successfully" in {
    val check: JavaCheckClass = classOf[TestCheck].asInstanceOf[JavaCheckClass]
    when(repo.rule("testRule")).thenAnswer(mock[NewRule])

    assert(definition.addRule(check).isRight)
  }
}

@Rule(key = "testRule")
class TestCheck extends JavaFileScanner {
  override def scanFile(context: JavaFileScannerContext): Unit = ???
}
