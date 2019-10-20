package io.github.zukkari.definition

import io.github.zukkari.BaseSpec
import org.mockito.MockitoSugar._
import org.sonar.plugins.java.api.CheckRegistrar.RegistrarContext

class SonarAcademicRulesRegistrarSpec extends BaseSpec {

  it should "register classes for provided registrar context" in {
    val context = spy(mock[RegistrarContext])

    doNothing.when(context).registerClassesForRepository(any[String], any, any)

    new SonarAcademicRulesRegistrar().register(context)

    verify(context, atLeastOnce).registerClassesForRepository(any[String], any, any)
  }

}
