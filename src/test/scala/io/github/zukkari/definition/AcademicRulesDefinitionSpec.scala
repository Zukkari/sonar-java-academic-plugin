package io.github.zukkari.definition

import io.github.zukkari.BaseSpec
import io.github.zukkari.config.{Java, LanguageKind}
import io.github.zukkari.config.Rules.JavaCheckClass
import org.mockito.MockitoSugar._
import org.sonar.api.server.rule.RulesDefinition
import org.sonar.api.server.rule.RulesDefinition.{NewRepository, NewRule}
import org.sonar.check.Rule
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}

class AcademicRulesDefinitionSpec extends BaseSpec {
  val context: RulesDefinition.Context = spy(mock[RulesDefinition.Context])
  implicit val repo: NewRepository = mock[NewRepository]

  val definition = new AcademicRulesDefinition

  it should "call create repository for context" in {
    when(context.createRepository(definition.repoKey, Java.key)).thenAnswer(repo)
    when(repo.setName(any)).thenAnswer(repo)

    val mockRule = mock[NewRule]
    when(repo.createRule(any)).thenAnswer(mockRule)
    when(repo.rule(any)).thenAnswer(mockRule)
    when(mockRule.key).thenAnswer("testRule")

    when(mockRule.setName(any)).thenCallRealMethod
    when(mockRule.setSeverity(any)).thenCallRealMethod
    when(mockRule.addTags(any)).thenAnswer(mockRule)
    when(mockRule.setType(any)).thenCallRealMethod
    when(mockRule.setStatus(any)).thenCallRealMethod
    when(mockRule.setHtmlDescription(any[String])).thenCallRealMethod

    definition.define(context)

    verify(context, atLeastOnce).createRepository(definition.repoKey, Java.key)
  }

  it should "create annotated rules successfully" in {
    val check: JavaCheckClass = classOf[TestCheck].asInstanceOf[JavaCheckClass]

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
