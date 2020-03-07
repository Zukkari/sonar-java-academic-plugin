package io.github.zukkari.checks

import cats.effect.IO
import io.github.zukkari.base.SensorRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.util.Log
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Configuration
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree._

import scala.jdk.CollectionConverters._
import io.github.zukkari.syntax.SymbolSyntax._
@Rule(key = "TraditionBreakerRule")
class TraditionBreakerRule extends JavaCheck with SensorRule {
  private var highNumberOfMembers: Double = _
  private var lowNumberOfMembers: Double = _

  private var classToParentContext: Map[String, String] = Map.empty
  private var classToFileContext: Map[String, InputFile] = Map.empty
  private var classToLineContext: Map[String, Int] = Map.empty
  private var classToMembersContext: Map[String, Int] = Map.empty

  override def configure(configuration: Configuration): Unit = {
    highNumberOfMembers = configuration
      .getDouble(
        ConfigurationProperties.TRADITION_BREAKER_HIGH_NUMBER_OF_MEMBERS.key)
      .orElse(
        ConfigurationProperties.TRADITION_BREAKER_HIGH_NUMBER_OF_MEMBERS.defaultValue.toDouble)

    lowNumberOfMembers = configuration
      .getDouble(
        ConfigurationProperties.TRADITION_BREAKER_LOW_NUMBER_OF_MEMBERS.key)
      .orElse(
        ConfigurationProperties.TRADITION_BREAKER_LOW_NUMBER_OF_MEMBERS.defaultValue.toDouble)
  }

  private def hasSubclasses(parent: String): Boolean =
    classToParentContext.exists { case (_, p) => p == parent }

  override def scan(t: Tree): Unit = {
    val visitor = new ParentAndMemberVisitor
    visitor.scan(t)

    classToParentContext ++= visitor.nameToParent
    classToLineContext ++= visitor.declarations
    classToMembersContext ++= visitor.nameToMembers

    classToFileContext ++= visitor.nameToParent.map {
      case (name, _) => (name, inputFile)
    }
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    classToMembersContext.foreach {
      case (c, members) =>
        for {
          parent <- classToParentContext.get(c)
          parentMembers <- classToMembersContext.get(parent)
          if parentMembers >= highNumberOfMembers && members <= lowNumberOfMembers && !hasSubclasses(
            c)
          file <- classToFileContext.get(c)
          line <- classToLineContext.get(c)
        } yield
          IO {
            report(
              sensorContext,
              "Tradition breaker",
              Declaration(file, line),
              TraditionBreakerRule.key
            )
          }.unsafeRunSync()
    }
  }
}

class ParentAndMemberVisitor extends SonarAcademicSubscriptionVisitor {
  private val log = Log(classOf[ParentAndMemberVisitor])

  var nameToParent: Map[String, String] = Map.empty
  var nameToMembers: Map[String, Int] = Map.empty
  var declarations: Map[String, Int] = Map.empty

  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS)

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]
    classTree.symbol().fullyQualifiedName match {
      case None            => super.visitNode(tree)
      case Some(className) =>
        // Add declaration so we can report an issue later if needed
        declarations += className -> classTree.firstToken.line

        // Find how many non-private methods does the class have
        val memberCount = classTree.members.asScala.toList.count {
          case v: VariableTree if !hasPrivateMember(v.modifiers) => true
          case m: MethodTree if !hasPrivateMember(m.modifiers)   => true
          case _                                                 => false
        }
        log.info(s"Class $className has $memberCount non-private members")
        nameToMembers += className -> memberCount

        nameToParent = Option(classTree.superClass)
          .map(_.symbolType())
          .map(_.symbol())
          .flatMap(_.fullyQualifiedName) match {
          case Some(parent) => nameToParent + (className -> parent)
          case _            => nameToParent
        }

        super.visitNode(tree)
    }
  }

  def scan(tree: Tree): Unit = scanTree(tree)

  private def hasPrivateMember(m: ModifiersTree): Boolean =
    m.modifiers.asScala.exists(_.modifier == Modifier.PRIVATE)
}

object TraditionBreakerRule {
  val key = "TraditionBreakerRule"
}
