package io.github.zukkari.checks

import io.github.zukkari.base.SensorRule
import io.github.zukkari.config.ConfigurationProperties
import io.github.zukkari.syntax.HierarchySyntax._
import io.github.zukkari.util.{HierarchyBuilder, Log}
import io.github.zukkari.visitor.SonarAcademicSubscriptionVisitor
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.config.Configuration
import org.sonar.check.Rule
import org.sonar.plugins.java.api.JavaCheck
import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{ClassTree, Tree}

object ParallelInheritanceHierarchies {
  val key = "ParallelInheritanceHierarchies"
}

@Rule(key = "ParallelInheritanceHierarchies")
class ParallelInheritanceHierarchies extends JavaCheck with SensorRule {
  private val log = Log(classOf[ParallelInheritanceHierarchies])

  private var prefixLength: Int = _
  private var hierarchyDepth: Int = _

  private var classToParentMap: Map[String, String] = Map.empty
  private var declarationMap: Map[String, Declaration] = Map.empty

  override def configure(configuration: Configuration): Unit = {
    prefixLength = configuration
      .getInt(
        ConfigurationProperties.PARALLEL_INHERITANCE_HIERARCHIES_PREFIX_LENGTH.key)
      .orElse(1)

    hierarchyDepth = configuration
      .getInt(
        ConfigurationProperties.PARALLEL_INHERITANCE_HIERARCHIES_HIERARCHY_DEPTH.key)
      .orElse(5)
  }

  override def scan(t: Tree): Unit = {
    val visitor = new HierarchyVisitor(inputFile)
    visitor.scanTree(tree = t)

    classToParentMap ++= visitor.classToParentMap
    declarationMap ++= visitor.declarationMap
  }

  override def afterAllScanned(sensorContext: SensorContext): Unit = {
    val hierarchyMap = new HierarchyBuilder().build(classToParentMap)

    hierarchyMap
      .filter {
        case (_, value) => value.length >= hierarchyDepth
      }
      .foreach {
        case (key, hierarchy) =>
          val prefix = key.substring(0, prefixLength)
          val matchingClass = hierarchyMap.find {
            case (matchingName, matchingHierarchy) =>
              key != matchingName && matchingName.startsWith(prefix) && hierarchy.length == matchingHierarchy.length
          }

          val implementation = hierarchy.implementation
          (matchingClass, declarationMap.get(implementation)) match {
            case (Some((className, matchingHierarchy)), Some(declaration)) =>
              log.info(
                s"Class $implementation and ${matchingHierarchy.implementation} have parallel hierarchies of depth ${matchingHierarchy.length}")

              report(
                sensorContext,
                s"Parallel hierarchy with class: '$className'",
                declaration,
                ParallelInheritanceHierarchies.key
              )
            case _ =>
          }
      }
  }
}
import io.github.zukkari.syntax.SymbolSyntax._

class HierarchyVisitor(val javaFile: InputFile)
    extends SonarAcademicSubscriptionVisitor {
  override def nodesToVisit: List[Tree.Kind] = List(Kind.CLASS, Kind.INTERFACE)

  var classToParentMap: Map[String, String] = Map.empty
  var declarationMap: Map[String, Declaration] = Map.empty

  override def visitNode(tree: Tree): Unit = {
    val classTree = tree.asInstanceOf[ClassTree]
    (classTree.symbol().fullyQualifiedName,
     Option(classTree.superClass)
       .map(_.symbolType())
       .map(_.symbol())
       .flatMap(_.fullyQualifiedName)) match {
      case (Some(name), Some(superClass)) =>
        classToParentMap += name -> superClass
        declarationMap += name -> Declaration(javaFile,
                                              classTree.firstToken.line)
        super.visitNode(tree)
      case _ => super.visitNode(tree)
    }
  }
}
