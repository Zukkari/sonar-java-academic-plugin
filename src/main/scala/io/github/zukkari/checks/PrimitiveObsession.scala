package io.github.zukkari.checks

import cats.Monoid
import cats.implicits._
import io.github.zukkari.base.SensorRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.SymbolSyntax._
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

@Rule(key = "PrimitiveObsession")
class PrimitiveObsession extends JavaCheck with SensorRule {
  private val log = Log(classOf[PrimitiveObsession])

  private var primitiveTimesUsed: Int = _
  private var packagesToIgnore: List[String] = Nil

  var classDeclarations: Set[ClassTree] = Set.empty

  var declarationMap: Map[VariableTree, Declaration] = Map.empty

  override def configure(configuration: Configuration): Unit = {
    primitiveTimesUsed = configuration
      .getInt(
        ConfigurationProperties.PRIMITIVE_OBSESSION_PRIMITIVE_TIMES_USED.key)
      .orElse(3)

    packagesToIgnore = configuration
      .getStringArray(
        ConfigurationProperties.PRIMITIVE_OBSESSION_IGNORED_PACKAGES.key)
      .toList

    packagesToIgnore = ConfigurationProperties.PRIMITIVE_OBSESSION_IGNORED_PACKAGES.defaultValue :: packagesToIgnore
  }

  override def scan(t: Tree): Unit = {
    val visitor = new PrimitiveObsessionVisitor(inputFile)
    visitor.visit(tree = t)

    classDeclarations ++= visitor.classDeclarations
    declarationMap ++= visitor.declarations
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    val declaredTypes = classDeclarations
      .map(c => c.symbol().fullyQualifiedName)
      .filter(_.nonEmpty)
      .map(_.get)

    log.info(s"Found ${declaredTypes.size} declared types")

    classDeclarations.foreach { classDeclaration =>
      classDeclaration.members.asScala
        .filter(_.is(Kind.VARIABLE))
        .map(_.asInstanceOf[VariableTree])
        .filter { variable =>
          // Check that this type was not declared during our search
          val maybeFullClassName = Option(variable.`type`)
            .map(_.symbolType())

          val notDeclared = !(declaredTypes contains maybeFullClassName
            .map(_.fullyQualifiedName())
            .getOrElse(""))

          val shouldIgnore = maybeFullClassName match {
            case Some(symbol) if !symbol.isPrimitive() =>
              packagesToIgnore.exists(symbol.fullyQualifiedName().startsWith(_))
            case Some(_) => true
            case _       => false
          }

          notDeclared && !shouldIgnore
        }
        .map {
          // Check that this variable is used by multiple methods
          variable =>
            val visitor = new VariableMemberSelectionVisitor(variable)
            visitor.visit(classDeclaration)

            (variable, visitor.count - 1) // Subtract one since declaration also counts as identifier
        }
        .filter {
          case (_, count) => count > primitiveTimesUsed
        }
        .map {
          case (variable, count) => (declarationMap.get(variable), count)
        }
        .filter {
          case (maybeDeclaration, _) => maybeDeclaration.nonEmpty
        }
        .map {
          case (maybeDeclaration, count) => (maybeDeclaration.get, count)
        }
        .foreach {
          case (declaration, count) =>
            // Primitive obsession found, report the issue
            report(
              sensorContext,
              s"Primitive obsession: externally declared class used $count times with max allowed $primitiveTimesUsed",
              declaration,
              "PrimitiveObsession"
            )
        }
    }
  }
}

class PrimitiveObsessionVisitor(inputFile: InputFile)
    extends SonarAcademicSubscriptionVisitor {

  var classDeclarations: Set[ClassTree] = Set.empty
  var declarations: Map[VariableTree, Declaration] = Map.empty

  override def nodesToVisit: List[Tree.Kind] =
    List(Kind.CLASS)

  override def visitNode(tree: Tree): Unit = {
    classDeclarations += tree.asInstanceOf[ClassTree]

    declarations ++= tree
      .asInstanceOf[ClassTree]
      .members
      .asScala
      .filter(_.is(Kind.VARIABLE))
      .map(_.asInstanceOf[VariableTree])
      .map(variable =>
        variable -> Declaration(inputFile, variable.firstToken.line))

    super.visitNode(tree)
  }
}

class VariableMemberSelectionVisitor(variable: VariableTree)(
    implicit m: Monoid[Int])
    extends SonarAcademicSubscriptionVisitor {
  var count: Int = m.empty

  override def nodesToVisit: List[Kind] =
    List(Kind.IDENTIFIER, Kind.EXPRESSION_STATEMENT)

  override def visitNode(tree: Tree): Unit = {
    count = tree match {
      case expressionTree: ExpressionTree
          if expressionTree.toString == variable.simpleName.toString =>
        count + 1
      case ident: IdentifierTree
          if ident.toString == variable.simpleName.toString =>
        count + 1
      case _ => count
    }

    super.visitNode(tree)
  }
}
