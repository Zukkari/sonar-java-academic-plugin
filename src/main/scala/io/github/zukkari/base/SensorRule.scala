package io.github.zukkari.base

import io.github.zukkari.checks.Declaration
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.rule.RuleKey
import org.sonar.plugins.java.api.tree.Tree

trait SensorRule {
  var inputFile: InputFile = _

  def scan(t: Tree): Unit

  def afterAllScanned(sensorContext: SensorContext): Unit

  def report(sensorContext: SensorContext,
             issue: String,
             declaration: Declaration,
             rule: String): Unit = {
    val newIssue = sensorContext.newIssue
      .forRule(RuleKey.of(SonarAcademicRulesDefinition.repoKey, rule))

    val location = newIssue
      .newLocation()
      .on(declaration.f)
      .at(declaration.f.selectLine(declaration.line))
      .message(issue)

    newIssue.at(location)
    newIssue.save()
  }
}
