package io.github.zukkari

import java.util.Optional

import io.github.zukkari.definition.SonarAcademicRulesDefinition
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar.when
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar
import org.sonar.api.config.Configuration

trait BaseSpec
    extends AnyFlatSpec
    with MockitoSugar
    with ArgumentMatchersSugar
    with BeforeAndAfter {

  before {
    val conf = config
    SonarAcademicRulesDefinition.configuration = conf
  }

  def config: Configuration = {
    val config = mock[Configuration]

    when(config.getDouble(any)).thenAnswer(Optional.empty[java.lang.Double]())
    when(config.getInt(any)).thenAnswer(Optional.empty[Integer]())

    config
  }
}
