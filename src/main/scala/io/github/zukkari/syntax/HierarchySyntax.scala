package io.github.zukkari.syntax

import io.github.zukkari.util.{Hierarchy, Implementation, Parent}

import scala.annotation.tailrec

object HierarchySyntax {

  implicit class HierarchyOps(hierarchy: Hierarchy) {
    def length: Int = {
      @tailrec
      def _len(hierarchy: Hierarchy, acc: Int): Int = {
        hierarchy match {
          case Implementation(_) => acc + 1
          case Parent(_, child)  => _len(child, acc + 1)
        }
      }

      _len(hierarchy, 0)
    }

    def implementation: String = {
      @tailrec
      def _impl(hierarchy: Hierarchy): String = {
        hierarchy match {
          case Implementation(impl) => impl
          case Parent(_, child)     => _impl(child)
        }
      }

      _impl(hierarchy)
    }

    def asString: Set[String] = {
      @tailrec
      def _asString(hierarchy: Hierarchy, acc: List[String]): List[String] = {
        hierarchy match {
          case Implementation(name) => name :: acc
          case Parent(name, child)  => _asString(child, name :: acc)
        }
      }

      _asString(hierarchy, Nil).toSet
    }
  }
}
