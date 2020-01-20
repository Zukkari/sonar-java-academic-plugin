package io.github.zukkari.checks

import cats.Monoid
import cats.implicits._
import io.github.zukkari.base.SensorRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.SymbolSyntax._
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Configuration
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree._

@Rule(key = "MissingTemplateMethod")
class MissingTemplateMethod extends JavaCheck with SensorRule {

  private var minimalCommonVariableAndMethodCount: Int = _
  private var minimalMethodCount: Int = _

  private var declarationMap: Map[String, Declaration] = Map.empty
  private var methodToUsageMap: Map[String, Set[String]] = Map.empty

  override def configure(configuration: Configuration): Unit = {
    minimalCommonVariableAndMethodCount = configuration
      .getInt(
        ConfigurationProperties.MISSING_TEMPLATE_METHOD_COMMON_MEMBERS.key)
      .orElse(
        ConfigurationProperties.MISSING_TEMPLATE_METHOD_COMMON_MEMBERS.defaultValue.toInt)

    minimalMethodCount = configuration
      .getInt(
        ConfigurationProperties.MISSING_TEMPLATE_METHOD_COMMON_METHODS.key
      )
      .orElse(
        ConfigurationProperties.MISSING_TEMPLATE_METHOD_COMMON_METHODS.defaultValue.toInt)
  }

  override def scan(t: Tree): Unit = {
    val visitor = new MissingTemplateMethodVisitor(inputFile)
    visitor.visit(tree = t)

    declarationMap ++= visitor.declarationMap
    methodToUsageMap ++= visitor.methodMemberUsageMap
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    declarationMap.keySet.toList
      .combinations(minimalMethodCount)
      .flatMap(_.map(method => (method, methodToUsageMap.get(method)))
        .traverse {
          case (first, second) =>
            second match {
              case Some(value) => (first, value).some
              case _           => none
            }
        })
      .filter { combination =>
        val (_, members) = combination.head
        combination.forall {
          case (_, parameters) => members == parameters
        }
      }
      .flatMap(_.map { case (name, _) => (name, declarationMap.get(name)) }
        .traverse {
          case (name, declaration) =>
            declaration match {
              case Some(declaration) => (name, declaration).some
              case _                 => none
            }
        })
      .foreach { combinations =>
        // Found all combinations with issues, now just report the problem
        combinations.foreach {
          case combination @ (_, declaration) =>
            val remaining = combinations diff List(combination)

            report(
              sensorContext,
              s"Missing template method: similar to method(s): ${remaining
                .map { case (name, _) => name }
                .mkString(", ")}",
              declaration,
              "MissingTemplateMethod"
            )
        }
      }
  }
}

class MissingTemplateMethodVisitor(val inputFile: InputFile)(
    implicit val m: Monoid[String])
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.METHOD)

  var methodMemberUsageMap: Map[String, Set[String]] = Map.empty
  var declarationMap: Map[String, Declaration] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val methodTree = tree.asInstanceOf[MethodTree]

    val symbolName =
      Option(methodTree.symbol)
        .flatMap(_.ownerAndSymbolName)
        .getOrElse(m.empty)

    val memberVisitor = new MissingTemplateMethodMemberSelectVisitor
    memberVisitor.visit(tree)

    val (newUsageMap, newDeclarationMap) = Option(memberVisitor.usedMembers)
      .filter(_.nonEmpty) match {
      case Some(members) =>
        (methodMemberUsageMap + (symbolName -> members),
         declarationMap + (symbolName -> Declaration(
           inputFile,
           methodTree.firstToken.line)))
      case _ => (methodMemberUsageMap, declarationMap)
    }

    methodMemberUsageMap = newUsageMap
    declarationMap = newDeclarationMap

    super.visitNode(tree)
  }
}

class MissingTemplateMethodMemberSelectVisitor
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] =
    List(Kind.METHOD_INVOCATION, Kind.MEMBER_SELECT)

  var usedMembers: Set[String] = Set.empty

  override def visitNode(tree: Tree): Unit = {
    val maybeMemberSelect = tree match {
      case memberSelect: MemberSelectExpressionTree =>
        Option(memberSelect.expression)
          .filter(_.isInstanceOf[IdentifierTree])
          .map(_.symbolType)
          .map(_.toString)
          .flatMap { variableName =>
            Option(memberSelect.identifier)
              .filter(_.isInstanceOf[IdentifierTree])
              .map(_.toString)
              .map { variableSelect =>
                s"$variableName#$variableSelect"
              }
          }
      case methodInvocation: MethodInvocationTree =>
        Option(methodInvocation.symbol)
          .flatMap(_.ownerAndSymbolName)
    }

    usedMembers = maybeMemberSelect match {
      case Some(value) => usedMembers + value
      case _           => usedMembers
    }

    super.visitNode(tree)
  }
}
