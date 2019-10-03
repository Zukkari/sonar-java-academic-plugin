package io.github.zukkari.checks

import java.util

import cats.implicits._
import cats.kernel.Monoid
import io.github.zukkari.BaseSpec
import org.mockito.MockitoSugar._
import org.sonar.java.checks.verifier.JavaCheckVerifier
import org.sonar.java.resolve.JavaSymbol.{MethodJavaSymbol, TypeJavaSymbol, VariableJavaSymbol}
import org.sonar.java.resolve.TypeVariableJavaType
import org.sonar.plugins.java.api.semantic.Symbol.TypeSymbol
import org.sonar.plugins.java.api.tree._

import scala.jdk.CollectionConverters._

class MessageChainSpec extends BaseSpec {
  val m: Monoid[String] = Monoid[String]
  val rule = new MessageChainRule

  it should "detect method call chains in the same file" in {
    verify("MessageChain")
  }

  it should "return depth when MethodInvocationTree is not followed by MethodJavaSymbol" in {
    val tree = mock[MethodInvocationTree]
    val typeSymbol = mock[TypeSymbol]

    when(tree.symbol).thenReturn(typeSymbol)

    val traversal = rule.depth(tree, Traversal(m.empty, -1))
    assert(traversal.depth == -1)
  }

  it should "return depth when size of block > 1 or return is not first statement of block" in {
    val methodJavaSymbol: MethodJavaSymbol = mock[MethodJavaSymbol]
    val declaration = mock[MethodTree]
    val block = mock[BlockTree]
    val body: util.List[StatementTree] = mock[util.List[StatementTree]]

    when(methodJavaSymbol.declaration).thenReturn(declaration)
    when(declaration.block).thenReturn(block)
    when(block.body).thenReturn(body)
    when(body.size()).thenReturn(2)

    assert(rule.depthSymbolTree(methodJavaSymbol, Traversal(m.empty, -1)).depth == -1)
  }

  it should "return depth if MethodInvocationTree is not followed by expressionTree or identifier tree" in {
    val tree = mock[MethodInvocationTree]
    val primitive = mock[PrimitiveTypeTree]
    when(tree.methodSelect).thenReturn(primitive)

    assert(rule.depthMethodInvocationTree(tree, Traversal(m.empty, -1)).depth == -1)
  }

  it should "return depth if identifier is not followed by variable" in {
    val ident = mock[IdentifierTree]
    val symbol = mock[MethodJavaSymbol]

    when(ident.symbol).thenReturn(symbol)

    assert(rule.depthIdentifier(ident, Traversal(m.empty, -1)).depth == -1)
  }

  it should "return depth if variable does not have class" in {
    val variable = mock[VariableJavaSymbol]
    val varType = mock[TypeVariableJavaType]

    when(variable.getType).thenReturn(varType)

    assert(rule.depthVariableSymbol(variable, Traversal(m.empty, -1)).depth == -1)
  }

  it should "return depth if class has no members" in {
    val javaSymbol = mock[TypeJavaSymbol]
    val classTree = mock[ClassTree]

    when(javaSymbol.declaration).thenReturn(classTree)
    when(classTree.members).thenReturn(Nil.asJava)

    assert(rule.depthType(javaSymbol, Traversal(m.empty, -1)).depth == -1)
  }

  it should "return depth when member selection expression is not followed by identifier" in {
    val tree = mock[MemberSelectExpressionTree]
    val primitiveTree = mock[PrimitiveTypeTree]

    when(tree.expression).thenReturn(primitiveTree)

    assert(rule.depthMemberSelectExpression(tree, Traversal(m.empty, -1)).depth == -1)
  }

  def verify(check: String): Unit = {
    JavaCheckVerifier.verify(s"src/test/resources/files/message_chains/$check.java", rule)
  }
}
