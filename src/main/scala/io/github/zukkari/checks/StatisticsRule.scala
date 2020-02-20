package io.github.zukkari.checks

import io.github.zukkari.base.SensorRule
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.{ClassTree, Tree}
import org.sonar.plugins.java.api.tree.Tree.Kind
import io.github.zukkari.syntax.SymbolSyntax._
import org.sonar.api.batch.fs.InputFile
import cats.implicits._
import scala.jdk.CollectionConverters._

@Rule(key = "StatisticsRule")
class StatisticsRule extends JavaCheck with SensorRule {
  private var classCount: Int = 0
  private var methodCount: Int = 0
  private var variableCount: Int = 0

  private var interfaceCount: Int = 0

  var declaration: Option[Declaration] = None

  override def scan(t: Tree): Unit = {
    val classCountVisitor = new ClassCountVisitor(inputFile)
    classCountVisitor.visit(t)

    classCount += classCountVisitor.classCount
    methodCount += classCountVisitor.methodCount
    variableCount += classCountVisitor.variableCount

    val interfaceCountVisitor = new InterfaceCountVisitor
    interfaceCountVisitor.visit(t)

    interfaceCount += interfaceCountVisitor.interfaceCount
    methodCount += interfaceCountVisitor.methodCount
    variableCount += interfaceCountVisitor.variableCount

    declaration = declaration match {
      case None => classCountVisitor.declaration
      case _    => declaration
    }
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    report(
      sensorContext,
      s"Classes:$classCount/Methods:$methodCount/Variables:$variableCount/Interfaces:$interfaceCount",
      declaration.get,
      "StatisticsRule")
  }
}

class ClassCountVisitor(val inputFile: InputFile)
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS)

  var declaration: Option[Declaration] = None

  var classCount = 0
  var variableCount = 0
  var methodCount = 0

  override def visitNode(tree: Tree): Unit = {
    classCount += 1

    val classTree = tree.asInstanceOf[ClassTree]

    variableCount += classTree.members.asScala.count(_.is(Kind.VARIABLE))
    methodCount += classTree.members.asScala.count(_.is(Kind.METHOD))

    classTree.symbol.fullyQualifiedName match {
      case Some(_) if declaration.isEmpty =>
        declaration = Declaration(inputFile, tree.firstToken.line).some
      case _ =>
    }

    super.visitNode(tree)
  }
}

class InterfaceCountVisitor extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Kind] = List(Kind.INTERFACE)

  var interfaceCount: Int = 0
  var methodCount: Int = 0
  var variableCount: Int = 0

  override def visitNode(tree: Tree): Unit = {
    interfaceCount += 1

    methodCount += tree
      .asInstanceOf[ClassTree]
      .members()
      .asScala
      .count(_.is(Kind.METHOD))

    variableCount +=
      tree
        .asInstanceOf[ClassTree]
        .members()
        .asScala
        .count(_.is(Kind.VARIABLE))

    super.visitNode(tree)
  }
}
