package io.github.zukkari.checks

import io.circe.generic.auto._
import io.circe.syntax._
import io.github.zukkari.base.{ComplexityAccessor, JavaRule}
import io.github.zukkari.common.{CohesionCalculator, InstructionCounter}
import io.github.zukkari.implicits._
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaFileScannerContext
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{
  ClassTree,
  MethodInvocationTree,
  MethodTree,
  Tree
}

import scala.jdk.CollectionConverters._

case class InterfaceStatistic(numberOfMethods: Int)

case class ClassStatistics(issueType: String,
                           attributes: Int,
                           methods: Int,
                           instructions: Int,
                           comments: Int,
                           complexity: Int,
                           complexityRatio: Double,
                           coupling: Int,
                           cohesion: Int)

case class MethodStatistics(issueType: String,
                            complexity: Int,
                            calls: Int,
                            instructions: Int,
                            parameters: Int,
                            chainLength: Int,
                            switchStatements: Int)

@Rule(key = "ClassStatsCollector")
class ClassStatsCollector extends JavaRule with ComplexityAccessor {
  private var context: JavaFileScannerContext = _

  override def scannerContext: JavaFileScannerContext = context

  override def scanFile(
    javaFileScannerContext: JavaFileScannerContext
  ): Unit = {
    this.context = javaFileScannerContext

    scan(context.getTree)
  }

  override def visitClass(tree: ClassTree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]

    if (!classTree.is(Kind.INTERFACE) && classTree.simpleName != null) {
      runClass(classTree)
    }

    super.visitClass(tree)
  }

  private def runClass(
    classTree: ClassTree
  )(implicit counter: InstructionCounter[MethodTree]): Unit = {
    val numberOfAttributes =
      classTree.members().asScala.count(_.is(Kind.VARIABLE))
    val numberOfMethods = classTree.members().asScala.count(_.is(Kind.METHOD))

    val methodStatistics = classTree.members.asScala
      .filter(_.is(Kind.METHOD))
      .map(_.asInstanceOf[MethodTree])
      .map { tree =>
        val numComplexity = complexity(tree)
        val calls = 0
        val instructions = counter.count(tree)
        val parameters = tree.symbol.parameterTypes().size()
        val switchStatements = {
          val visitor = new SwitchVisitor
          visitor.visit(tree)
          visitor.count
        }
        val maxChain = {
          val visitor = new CollectorMethodInvocationVisitor()
          visitor.visit(tree)
          visitor.maxChain
        }

        MethodStatistics(
          "method",
          numComplexity,
          calls,
          instructions,
          parameters,
          maxChain,
          switchStatements
        )
      }

    val numOfInstructions = methodStatistics.map(_.instructions).sum
    val numOfComments = {
      val visitor = new CollectorsCommentCounter
      visitor.visit(classTree)
      visitor.count
    }

    val totalComplexity = methodStatistics.map(_.complexity).sum
    val complexityRatio = totalComplexity / methodStatistics.size.toDouble
      .max(1.0)

    val cohesion = new CohesionCalculator().calculate(classTree)

    val coupling = 0

    val stats = ClassStatistics(
      "class",
      numberOfAttributes,
      numberOfMethods,
      numOfInstructions,
      numOfComments,
      totalComplexity,
      complexityRatio,
      coupling,
      cohesion
    ).asJson
      .toString()

    report(stats, classTree)

    methodStatistics.foreach(
      issue => report(issue.asJson.toString(), classTree)
    )
  }
}

class CollectorMethodInvocationVisitor
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] = List(Kind.METHOD_INVOCATION)

  var maxChain = 0

  override def visitNode(tree: Tree): Unit = {
    val invocationTree = tree.asInstanceOf[MethodInvocationTree]

    val newDepth = new MessageChainCalculator().calculate(invocationTree).depth
    maxChain = if (newDepth > maxChain) newDepth else maxChain

    super.visitNode(tree)
  }
}

class SwitchVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] =
    List(Kind.SWITCH_STATEMENT, Kind.SWITCH_EXPRESSION)

  var count = 0

  override def visitNode(tree: Tree): Unit = {
    count += 1

    super.visitNode(tree)
  }
}

class CollectorsCommentCounter extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] = List(Kind.TRIVIA)

  var count = 0

  override def visitNode(tree: Tree): Unit = {
    count += 1
    super.visitNode(tree)
  }
}
