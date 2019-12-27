package io.github.zukkari.config

import io.github.zukkari.BaseSpec
import io.github.zukkari.checks._

class RulesSpec extends BaseSpec {

  it should "contain 'data class' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[DataClassRule])
  }

  it should "contain 'message chain' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[MessageChainRule])
  }

  it should "contain 'long method' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[LongMethodRule])
  }

  it should "contain 'switch statement' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[SwitchStatementRule])
  }

  it should "contain 'shotgun surgery' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[ShotgunSurgeryRule])
  }

  it should "contain 'lazy class' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[LazyClass])
  }

  it should "contain 'blob class' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[BlobClass])
  }

  it should "contain 'refused bequest' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[RefusedBequest])
  }

  it should "contain 'comments detection' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[CommentDetectionRule])
  }

  it should "contain 'cyclic dependencies' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[CyclicDependenciesRule])
  }

  it should "contain 'tradition breaker' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[TraditionBreakerRule])
  }

  it should "contain 'divergent change' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[DivergentChange])
  }

  it should "contain 'feature envy' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[FeatureEnvy])
  }

  it should "contain 'data clump' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[DataClump])
  }

  it should "contain 'parallel inheritance hierarchies' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[ParallelInheritanceHierarchies])
  }

  it should "contain 'speculative generality (interfaces)' rule" in {
    val rules = Rules.get

    assert(rules contains classOf[SpeculativeGeneralityInterfaces])
  }

  it should "have constant size so we dont forget this test when we add new rule" in {
    val rules = Rules.get

    assert(rules.size == 17)
  }
}
