package io.github.zukkari.checks

import cats.effect.SyncIO
import io.github.zukkari.checks.DataClassSyntax._
import io.github.zukkari.util.{Log, Logger}
import org.sonar.check.Rule
import org.sonar.plugins.java.api.tree._
import org.sonar.plugins.java.api.{JavaFileScanner, JavaFileScannerContext}

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

@Rule(key = "DataClassRule", name = "Data class")
class DataClassRule extends JavaRule {
  private val log: Logger = Log(classOf[DataClassRule])

  private var context: JavaFileScannerContext = _

  override def scanFile(context: JavaFileScannerContext): Unit = {
    this.context = context

    scan(context.getTree)
  }

  /**
   * Two scenarios when this rule is true:
   *
   * 1. Class has no methods and some of the variables are public
   * 2. Class has only methods that start with get/set
   *
   * @param tree to verify against Data class code smell
   */
  override def visitClass(tree: ClassTree): Unit = {
    log.info(() => "Running data class rule...")
    val treeMembers = tree.members.asScala.toList

    implicit val classVarNames: List[String] = treeMembers
      .filter(_.isInstanceOf[VariableTree])
      .map(_.asInstanceOf[VariableTree].simpleName.name)
    log.info(() => s"Class variable names: $classVarNames")

    val methods = treeMembers
      .filter(_.isInstanceOf[MethodTree])
      .map(_.asInstanceOf[MethodTree])
    log.info(() => s"Class has the following methods: $methods")

    val getters = methods.getters
    log.info(() => s"Found ${getters.size} getters:")
    getters.foreach(m => log.info(() => m.toString))

    val setters = methods.setters
    log.info(() => s"Found ${setters.size} setters:")
    setters.foreach(m => log.info(() => m.toString))

    report(
      "Refactor this class so it includes more than just data",
      tree,
      getters.size + setters.size == methods.size && classVarNames.nonEmpty
    )

    val childClasses = treeMembers.filter(_.isInstanceOf[ClassTree]).map(_.asInstanceOf[ClassTree])
    runChildren(childClasses)
  }

  @tailrec
  private final def runChildren(classes: List[ClassTree]): Unit = classes match {
    case x :: xs =>
      visitClass(x)
      runChildren(xs)
    case Nil =>
  }

  override def scannerContext: JavaFileScannerContext = context
}

object DataClassSyntax {

  implicit class DataClassOps(methods: List[MethodTree]) {
    def getters(implicit classVarNames: List[String]): List[ExpressionTree] =
      methods
        .map(_.block.body)
        .filter(body => body.size == 1 && body.get(0).isInstanceOf[ReturnStatementTree])
        .map(_.get(0).asInstanceOf[ReturnStatementTree].expression)
        .filter(expr => expr.isInstanceOf[IdentifierTree] && (classVarNames contains expr.asInstanceOf[IdentifierTree].name))

    def setters(implicit classVarNames: List[String]): List[ExpressionTree] = {
      val assignmentExpression = methods
        .filter(method =>
          method.block.body.size == 1
            && method.block.body.get(0).isInstanceOf[ExpressionStatementTree])
        .map(_.block.body.get(0).asInstanceOf[ExpressionStatementTree].expression)
        .filter(_.isInstanceOf[AssignmentExpressionTree])
        .map(_.asInstanceOf[AssignmentExpressionTree].variable)

      assignmentExpression
        .filter(expr => expr.isInstanceOf[MemberSelectExpressionTree])
        .map(_.asInstanceOf[MemberSelectExpressionTree])
        .map(expr => (expr.expression.asInstanceOf[IdentifierTree], expr.identifier, expr))
        .filter { case (ident, member, _) =>
          (ident.name == "this") && (classVarNames contains member.name)
        }
        .map(_._3) ++
        assignmentExpression
          .filter(_.isInstanceOf[IdentifierTree])
          .map(expr => (expr.asInstanceOf[IdentifierTree].name, expr))
          .filter {
            case (varName, _) => classVarNames contains varName
          }
          .map(_._2)
    }
  }

}
