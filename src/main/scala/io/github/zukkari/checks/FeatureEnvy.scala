package io.github.zukkari.checks

import io.github.zukkari.base.JavaRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.util.Log
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree._

import scala.jdk.CollectionConverters._

@Rule(key = "FeatureEnvy")
class FeatureEnvy extends JavaRule {
  private val log = Log(classOf[FeatureEnvy])

  private var context: JavaFileScannerContext = _

  private var localityThreshold: Double = _
  private var accessToForeignClasses: Int = _
  private var accessToForeignVariables: Int = _

  override def scanFile(
      javaFileScannerContext: JavaFileScannerContext): Unit = {
    localityThreshold = config
      .getDouble(ConfigurationProperties.FEATURE_ENVY_LOCALITY_THRESHOLD.key)
      .orElse(0.33)

    accessToForeignClasses = config
      .getInt(
        ConfigurationProperties.FEATURE_ENVY_ACCESS_TO_FOREIGN_CLASSES.key)
      .orElse(2)

    accessToForeignVariables = config
      .getInt(
        ConfigurationProperties.FEATURE_ENVY_ACCESS_TO_FOREIGN_VARIABLES.key)
      .orElse(2)

    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def scannerContext: JavaFileScannerContext = context

  override def visitMethod(tree: MethodTree): Unit = {
    Option(tree.symbol).map(_.toString.split("#")(0)) match {
      case None =>
        log.info(s"Could not determine parent for method tree: $tree")
        super.visitMethod(tree)
      case Some(owner) =>
        val localVariables: Set[String] = Option(tree.parent)
          .filter(_.isInstanceOf[ClassTree])
          .map(_.asInstanceOf[ClassTree])
          .map(_.members.asScala.toSet)
          .map(
            members =>
              members
                .filter(_.is(Kind.VARIABLE))
                .map(_.asInstanceOf[VariableTree])
                .map(
                  variable =>
                    Option(variable.`type`)
                      .map(_.symbolType)
                      .map(_.toString)
                      .getOrElse(""))) match {
          case Some(variableTypes) => variableTypes
          case None                => Set.empty
        }

        val locator = new ForeignVariableUsageLocator(owner, localVariables)
        locator.scanTree(tree)

        val classes = locator.foreignClasses
        val variableUsage = locator.foreignVariableUsage

        val foreignVariables = classes.diff(localVariables)

        val localVariableCount = localVariables.size.min(1)
        report(
          "Feature envy",
          tree,
          variableUsage > accessToForeignVariables &&
            (localVariableCount * 1.0) / localVariableCount + foreignVariables.size > localityThreshold &&
            foreignVariables.size <= accessToForeignClasses
        )

        super.visitMethod(tree)
    }
  }
}

class ForeignVariableUsageLocator(val owner: String, val known: Set[String])
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] =
    List(Kind.METHOD_INVOCATION, Kind.MEMBER_SELECT)

  var foreignVariableUsage = 0
  var foreignClasses: Set[String] = Set.empty

  override def visitNode(tree: Tree): Unit = tree match {
    case invocation: MethodInvocationTree =>
      val (classes, variables) = {
        Option(invocation)
          .filter(
            invoke =>
              !Option(invoke.symbol)
                .map(_.toString)
                .getOrElse("")
                .contains(owner)) // Owner is not class under observation
          .filter(invoke =>
            Option(invoke.symbolType)
              .map(_.toString)
              .getOrElse("") != "void") // Return type is not void
          .map(_.symbol)
          .map(_.owner)
          .map(_.toString) match {
          case Some(value) if !known.contains(value) =>
            (foreignClasses + value, foreignVariableUsage + 1)
          case _ => (foreignClasses, foreignVariableUsage)
        }
      }

      foreignClasses = classes
      foreignVariableUsage = variables

      super.visitNode(tree)
    case select: MemberSelectExpressionTree =>
      val (classes, variables) = Option(select)
        .filter(sel =>
          Option(sel.expression).map(_.toString).getOrElse("") != "this")
        .flatMap(
          sel =>
            Option(sel.expression)
              .filterNot(_.is(Kind.NEW_CLASS))
              .map(_.symbolType)
              .map(_.toString)) match {
        case Some(exprOwner) if !known.contains(exprOwner) =>
          (foreignClasses + exprOwner, foreignVariableUsage + 1)
        case _ => (foreignClasses, foreignVariableUsage)
      }

      foreignClasses = classes
      foreignVariableUsage = variables

      super.visitNode(tree)
    case _ => super.visitNode(tree)
  }

}
