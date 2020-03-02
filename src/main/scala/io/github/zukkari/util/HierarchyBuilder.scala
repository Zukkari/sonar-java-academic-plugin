package io.github.zukkari.util

import scala.annotation.tailrec

sealed abstract class Hierarchy

case class Implementation(className: String) extends Hierarchy

case class Parent(className: String, childElement: Hierarchy) extends Hierarchy

class HierarchyBuilder {
  def build(classMap: Map[String, String]): Map[String, Hierarchy] = {
    classMap.map {
      case (clazz, _) =>
        clazz -> mkHierarchy(classMap, clazz)
    }
  }

  private def mkHierarchy(classMap: Map[String, String],
                          className: String): Hierarchy = {
    @tailrec
    def hierarchy(clazz: Option[String], acc: Hierarchy): Hierarchy = {
      clazz match {
        case Some(name) =>
          hierarchy(classMap.get(name), Parent(name, acc))
        case None => acc
      }
    }

    hierarchy(classMap.get(className), Implementation(className))
  }
}
