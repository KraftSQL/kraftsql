package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.And
import rocks.frieler.kraftsql.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNotNull
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.expressions.Sum

class SubexpressionCollectorTest {
    private val subexpressionCollector = GenericSubexpressionCollector<DummyEngine>()

    @Test
    fun `GenericSubexpressionCollector can collect left and right side of And`() {
        val and = And(mock<Expression<DummyEngine, Boolean>>(), mock<Expression<DummyEngine, Boolean>>())

        val subexpressions = subexpressionCollector.getSubexpressions(and)

        subexpressions shouldContainExactlyInAnyOrder listOf(and.left, and.right)
    }

    @Test
    fun `GenericSubexpressionCollector can collect elements of Array`() {
        val element1 = mock<Expression<DummyEngine, Int>>()
        val element2 = mock<Expression<DummyEngine, Int>>()
        val array = rocks.frieler.kraftsql.expressions.Array(arrayOf(element1, element2))

        val subexpressions = subexpressionCollector.getSubexpressions(array)

        subexpressions shouldContainExactlyInAnyOrder listOf(element1, element2)
    }

    @Test
    fun `GenericSubexpressionCollector can collect elements of NULL Array`() {
        val array = rocks.frieler.kraftsql.expressions.Array<DummyEngine, Int>(null)

        val subexpressions = subexpressionCollector.getSubexpressions(array)

        subexpressions.shouldBeEmpty()
    }

    @Test
    fun `GenericSubexpressionCollector can collect arguments of ArrayConcatenation`() {
        val element1 = mock<Expression<DummyEngine, Array<Any?>>>()
        val element2 = mock<Expression<DummyEngine, Array<Any?>>>()
        val arrayConcatenation = object : ArrayConcatenation<DummyEngine, Any?>(arrayOf(element1, element2)) {
            override fun sql(): String = TODO("Not yet implemented")
            override fun defaultColumnName(): String = TODO("Not yet implemented")
        }

        val subexpressions = subexpressionCollector.getSubexpressions(arrayConcatenation)

        subexpressions shouldContainExactlyInAnyOrder arrayConcatenation.arguments.toList()
    }

    @Test
    fun `GenericSubexpressionCollector can collect array expression of ArrayLength`() {
        val arrayExpression = mock<Expression<DummyEngine, Array<*>>>()
        val arrayLength = rocks.frieler.kraftsql.expressions.ArrayLength(arrayExpression)

        val subexpressions = subexpressionCollector.getSubexpressions(arrayLength)

        subexpressions shouldContainExactlyInAnyOrder listOf(arrayLength.array)
    }

    @Test
    fun `GenericSubexpressionCollector can collect expression of Cast`() {
        val cast = Cast<DummyEngine, String>(mock<Expression<DummyEngine, Any?>>(), mock())

        val subexpressions = subexpressionCollector.getSubexpressions(cast)

        subexpressions shouldContainExactlyInAnyOrder listOf(cast.expression)
    }

    @Test
    fun `GenericSubexpressionCollector can collect expressions of Coalesce`() {
        val coalesce = Coalesce(mock<Expression<DummyEngine, Any?>>(), mock<Expression<DummyEngine, Any?>>())

        val subexpressions = subexpressionCollector.getSubexpressions(coalesce)

        subexpressions shouldContainExactlyInAnyOrder coalesce.expressions
    }

    @Test
    fun `GenericSubexpressionCollector returns empty list for Column`() {
        val column = Column<DummyEngine, Int>("test_column")

        val subexpressions = subexpressionCollector.getSubexpressions(column)

        subexpressions.shouldBeEmpty()
    }

    @Test
    fun `GenericSubexpressionCollector returns empty list for Constant`() {
        val constant = Constant<DummyEngine, Int>(42)

        val subexpressions = subexpressionCollector.getSubexpressions(constant)

        subexpressions.shouldBeEmpty()
    }

    @Test
    fun `GenericSubexpressionCollector can collect expression of Count`() {
        val count = Count(mock<Expression<DummyEngine, Any?>>())

        val subexpressions = subexpressionCollector.getSubexpressions(count)

        subexpressions shouldContainExactlyInAnyOrder listOf(count.expression)
    }

    @Test
    fun `GenericSubexpressionCollector returns empty list for Count without expression`() {
        val count = Count<DummyEngine>()

        val subexpressions = subexpressionCollector.getSubexpressions(count)

        subexpressions.shouldBeEmpty()
    }

    @Test
    fun `GenericSubexpressionCollector can collect left and right of Equals`() {
        val equals = Equals(mock<Expression<DummyEngine, Any?>>(), mock<Expression<DummyEngine, Any?>>())

        val subexpressions = subexpressionCollector.getSubexpressions(equals)

        subexpressions shouldContainExactlyInAnyOrder listOf(equals.left, equals.right)
    }

    @Test
    fun `GenericSubexpressionCollector can collect expression of IsNotNull`() {
        val isNotNull = IsNotNull(mock<Expression<DummyEngine, Any?>>())

        val subexpressions = subexpressionCollector.getSubexpressions(isNotNull)

        subexpressions shouldContainExactlyInAnyOrder listOf(isNotNull.expression)
    }

    @Test
    fun `GenericSubexpressionCollector can collect values of Row`() {
        val row = Row<DummyEngine, Any>(mapOf(
            "key1" to mock<Expression<DummyEngine, Any?>>(),
            "key2" to mock<Expression<DummyEngine, Any?>>(),
        ))

        val subexpressions = subexpressionCollector.getSubexpressions(row)

        subexpressions shouldContainExactlyInAnyOrder row.values!!.values.toList()
    }

    @Test
    fun `GenericSubexpressionCollector can collect values of NULL Row`() {
        val row = Row<DummyEngine, Any>(null)

        val subexpressions = subexpressionCollector.getSubexpressions(row)

        subexpressions.shouldBeEmpty()
    }

    @Test
    fun `GenericSubexpressionCollector can collect expression of Sum`() {
        val sum = Sum(mock<Expression<DummyEngine, Int>>())

        val subexpressions = subexpressionCollector.getSubexpressions(sum)

        subexpressions shouldContainExactlyInAnyOrder listOf(sum.expression)
    }

    @Test
    fun `GenericSubexpressionCollector cannot collect subexpressions of unknown Expression`() {
        val unknownExpression = mock<Expression<DummyEngine, Any?>>()

        shouldThrow<NotImplementedError> {
            subexpressionCollector.getSubexpressions(unknownExpression)
        }
    }

    @Test
    fun `SubexpressionCollector can collect all expressions in an Expression tree`() {
        val subexpressionCollector = mock<SubexpressionCollector<DummyEngine>> {
            whenever(it.collectAllSubexpressions(any())).thenCallRealMethod()
        }
        val rootExpression = mock<Expression<DummyEngine, Any?>>()
        val leftChild = mock<Expression<DummyEngine, Any?>>()
        val rightChild = mock<Expression<DummyEngine, Any?>>()
        val grandChild = mock<Expression<DummyEngine, Any?>>()

        whenever(subexpressionCollector.getSubexpressions(rootExpression)).thenReturn(listOf(leftChild, rightChild))
        whenever(subexpressionCollector.getSubexpressions(leftChild)).thenReturn(listOf(grandChild))
        whenever(subexpressionCollector.getSubexpressions(rightChild)).thenReturn(emptyList())
        whenever(subexpressionCollector.getSubexpressions(grandChild)).thenReturn(emptyList())

        val subexpression = subexpressionCollector.collectAllSubexpressions(rootExpression)

        subexpression shouldContainExactlyInAnyOrder listOf(rootExpression, leftChild, rightChild, grandChild)
    }
}
