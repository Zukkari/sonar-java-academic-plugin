package io.github.zukkari.config

import io.github.zukkari.checks._
import io.github.zukkari.config.Rules.JavaCheckClass
import org.sonar.api.server.rule.RulesDefinition.NewRepository
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader
import org.sonar.api.utils.AnnotationUtils
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck

object Rules {
  type JavaCheckClass = Class[_ <: JavaCheck]

  def get: List[JavaCheckClass] = List(
    classOf[DataClassRule],
    classOf[MessageChainRule],
    classOf[LongMethodRule],
    classOf[SwitchStatementRule],
    classOf[ShotgunSurgeryRule],
    classOf[LongParameterList],
    classOf[LazyClass],
    classOf[BlobClass],
    classOf[RefusedBequest],
    classOf[CommentDetectionRule],
    classOf[CyclicDependenciesRule],
    classOf[TraditionBreakerRule],
    classOf[DivergentChange],
    classOf[FeatureEnvy],
    classOf[DataClump],
    classOf[ParallelInheritanceHierarchies],
    classOf[SpeculativeGeneralityInterfaces],
    classOf[SpeculativeGeneralityMethods],
    classOf[MiddleMan],
    classOf[PrimitiveObsession],
    classOf[BrainMethod],
    classOf[InappropriateIntimacy],
    classOf[AlternativeClassesWithDifferentInterfaces]
  )

}

object RulesSyntax {

  implicit class RulesCreatorOps(check: JavaCheckClass) {
    def makeRule(implicit repo: NewRepository,
                 loader: RulesDefinitionAnnotationLoader): Unit =
      loader.load(repo, check)
  }

  implicit class AnnotationOps(checkClass: JavaCheckClass) {
    def annotation: Rule =
      AnnotationUtils.getAnnotation(checkClass, classOf[Rule])
  }

}
