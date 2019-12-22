package io.github.zukkari.checks

import java.util

import cats.effect.IO
import io.github.zukkari.base.SensorRule
import io.github.zukkari.definition.SonarAcademicRulesDefinition
import io.github.zukkari.util.Log
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.rule.RuleKey
import org.sonar.check.Rule
import org.sonar.java.ast.visitors.SubscriptionVisitor
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree._

import scala.jdk.CollectionConverters._


@Rule(key = "TraditionBreakerRule")
class TraditionBreakerRule extends JavaCheck with SensorRule {
  private val highNumberOfMembers = 20
  private val lowNumberOfMembers = 5

  private var classToParentContext: Map[String, String] = Map.empty
  private var classToFileContext: Map[String, InputFile] = Map.empty
  private var classToLineContext: Map[String, Int] = Map.empty
  private var classToMembersContext: Map[String, Int] = Map.empty

  override def scan(f: InputFile, t: Tree): Unit = {
    val visitor = new ParentAndMemberVisitor
    visitor.scan(t)

    classToParentContext ++= visitor.nameToParent
    classToLineContext ++= visitor.declarations
    classToMembersContext ++= visitor.nameToMembers

    classToFileContext ++= visitor.nameToParent.map { case (name, _) => (name, f) }
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    classToMembersContext.foreach {
      case (c, members) =>
        for {
          parent <- classToParentContext.get(c)
          parentMembers <- classToMembersContext.get(parent)
          if parentMembers >= highNumberOfMembers && members <= lowNumberOfMembers
          file <- classToFileContext.get(c)
          line <- classToLineContext.get(c)
        } yield IO {
          val issue = sensorContext.newIssue
            .forRule(RuleKey.of(SonarAcademicRulesDefinition.repoKey, TraditionBreakerRule.key))

          val location = issue.newLocation()
            .on(file)
            .at(file.selectLine(line))
            .message("Tradition breaker")

          issue.at(location)
          issue.save()
        }.unsafeRunSync()
    }
  }
}

class ParentAndMemberVisitor extends SubscriptionVisitor {
  private val log = Log(classOf[ParentAndMemberVisitor])

  var nameToParent: Map[String, String] = Map.empty
  var nameToMembers: Map[String, Int] = Map.empty
  var declarations: Map[String, Int] = Map.empty

  override def nodesToVisit(): util.List[Tree.Kind] = List(Kind.CLASS).asJava

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]
    val className = classTree.simpleName.name

    // Add declaration so we can report an issue later if needed
    declarations += className -> classTree.firstToken.line

    // Find how many non-private methods does the class have
    val memberCount = classTree.members
      .asScala
      .toList.count {
      case v: VariableTree if !hasPrivateMember(v.modifiers) => true
      case m: MethodTree if !hasPrivateMember(m.modifiers) => true
      case _ => false
    }
    log.info(s"Class $className has $memberCount non-private members")
    nameToMembers += className -> memberCount

    nameToParent = Option(classTree.superClass).filter(_.isInstanceOf[IdentifierTree]).map(_.asInstanceOf[IdentifierTree].name) match {
      case Some(parent) => nameToParent + (className -> parent)
      case _ => nameToParent
    }

    super.visitNode(tree)
  }

  def scan(tree: Tree): Unit = scanTree(tree)

  private def hasPrivateMember(m: ModifiersTree): Boolean = m.modifiers.asScala.exists(_.modifier == Modifier.PRIVATE)
}

object TraditionBreakerRule {
  val key = "TraditionBreakerRule"
}
