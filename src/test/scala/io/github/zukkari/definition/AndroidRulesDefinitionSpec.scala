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

    val rule = mock[NewRule]
    when(repo.createRule(any)).thenAnswer(rule)
    when(rule.setName(any)).thenAnswer(rule)

    definition.define(context)

    verify(context, atLeastOnce).createRepository(definition.repoKey, Language.Java)
  }

  it should "create annotated rules successfully" in {
    val check: JavaCheckClass = classOf[TestCheck].asInstanceOf[JavaCheckClass]
    when(repo.rule("testRule")).thenAnswer(mock[NewRule])

    assert(definition.addRule(check).isRight)
  }

  it should "not find annotation if it is not present" in {
    val check: JavaCheckClass = classOf[NoAnnotationCheck].asInstanceOf[JavaCheckClass]
    assert(definition.getRuleAnnotation(check).isLeft)
  }

  it should "find annotation if it is present" in {
    val check: JavaCheckClass = classOf[TestCheck].asInstanceOf[JavaCheckClass]
    assert(definition.getRuleAnnotation(check).isRight)
  }

  it should "not find key if it is not present in annotation" in {
    val check: JavaCheckClass = classOf[NoAnnotationCheck].asInstanceOf[JavaCheckClass]
    assert(definition.getRuleAnnotation(check).isLeft)
  }

  it should "find key if it is present in annotation" in {
    val check: JavaCheckClass = classOf[TestCheck].asInstanceOf[JavaCheckClass]
    assert(definition.getRuleAnnotation(check).isRight)
  }
}

@Rule(key = "testRule")
class TestCheck extends JavaFileScanner {
  override def scanFile(context: JavaFileScannerContext): Unit = ???
}

class NoAnnotationCheck extends JavaFileScanner {
  override def scanFile(context: JavaFileScannerContext): Unit = ???
}

@Rule
class NoKeyWithAnnotation extends JavaFileScanner {
  override def scanFile(context: JavaFileScannerContext): Unit = ???
}
