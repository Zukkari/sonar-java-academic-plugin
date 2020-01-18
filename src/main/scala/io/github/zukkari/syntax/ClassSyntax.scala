package io.github.zukkari.syntax

import org.sonar.plugins.java.api.tree.Tree.Kind
import org.sonar.plugins.java.api.tree.{ClassTree, MethodTree, VariableTree}

import scala.jdk.CollectionConverters._

object ClassSyntax {

  implicit class ClassOps(classTree: ClassTree) {
    def methods: Iterable[MethodTree] =
      classTree.members.asScala
        .filter(_.is(Kind.METHOD))
        .map(_.asInstanceOf[MethodTree])

    def variables: Iterable[VariableTree] =
      classTree.members.asScala
        .filter(_.is(Kind.VARIABLE))
        .map(_.asInstanceOf[VariableTree])
  }

}
