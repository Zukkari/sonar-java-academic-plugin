package io.github.zukkari.config

import io.github.zukkari.checks.DataClassRule
import org.sonar.plugins.java.api.JavaCheck


object Rules {
  type JavaCheckClass = Class[JavaCheck]

  def get: List[JavaCheckClass] = List(
    classOf[DataClassRule]
  ).map(_.asInstanceOf[JavaCheckClass])

}
