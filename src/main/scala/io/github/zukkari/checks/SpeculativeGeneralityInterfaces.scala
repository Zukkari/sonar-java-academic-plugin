package io.github.zukkari.checks

import io.github.zukkari.base.SensorRule
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{ClassTree, Tree}

import scala.jdk.CollectionConverters._
import io.github.zukkari.syntax.SymbolSyntax._
import cats.implicits._

object SpeculativeGeneralityInterfaces {
  val key = "SpeculativeGeneralityInterfaces"
}

@Rule(key = "SpeculativeGeneralityInterfaces")
class SpeculativeGeneralityInterfaces extends JavaCheck with SensorRule {
  type Interfaces = List[String]
  type Class = String

  private var interfaceMap: Map[String, Declaration] = Map.empty
  private var implementationMap: Map[Class, Interfaces] = Map.empty

  override def scan(t: Tree): Unit = {
    val interfaceVisitor = new InterfaceVisitor(inputFile)
    interfaceVisitor.scanTree(tree = t)

    interfaceMap ++= interfaceVisitor.declarationMap

    val implementationVisitor = new InterfaceImplementationVisitor
    implementationVisitor.scanTree(tree = t)

    implementationMap ++= implementationVisitor.implementationMap
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    val allImplementations =
      implementationMap.values.toSet.foldLeft(Set.empty[String])(_ ++ _)

    interfaceMap.foreach {
      case (interface, declaration)
          if !(allImplementations contains interface) =>
        report(
          sensorContext,
          "Speculative generality: provide at least one implementation for this interface",
          declaration,
          SpeculativeGeneralityInterfaces.key
        )
      case _ =>
    }
  }

}

class InterfaceVisitor(val javaFile: InputFile)
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.INTERFACE)

  var declarationMap: Map[String, Declaration] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]

    declarationMap = {
      classTree.symbol().fullyQualifiedName match {
        case Some(name) =>
          declarationMap + (name -> Declaration(
            javaFile,
            classTree.firstToken.line
          ))
        case None => declarationMap
      }
    }

    super.visitNode(tree)
  }
}

class InterfaceImplementationVisitor extends SonarAcademicSubscriptionVisitor {
  type Interfaces = List[String]
  type Class = String

  override def nodesToVisit: List[Kind] = List(Kind.CLASS, Kind.INTERFACE)

  var implementationMap: Map[Class, Interfaces] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]

    implementationMap = (
      classTree.symbol().fullyQualifiedName,
      Option(classTree.superInterfaces)
        .map(
          interfaces =>
            interfaces.asScala.toList
              .map(
                interface =>
                  Option(interface.symbolType())
                    .map(_.symbol())
                    .flatMap(_.fullyQualifiedName)
              )
              .traverse(identity)
              .getOrElse(Nil)
        )
    ) match {
      case (Some(name), Some(interfaces)) if interfaces.nonEmpty =>
        implementationMap + (name -> interfaces)
      case _ => implementationMap
    }

    super.visitNode(tree)
  }
}
