package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.ArrayElementReference.Companion.get
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import kotlin.Array

class ArrayElementReferenceTest {
    @Test
    fun `SQL references element at index of array expression`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, Array<Any>>> {
            whenever(it.sql()).thenReturn("array")
        }
        val indexExpression = mock<Expression<TestableDummyEngine, Int>> {
            whenever(it.sql()).thenReturn("index")
        }

        val elementReference = ArrayElementReference(arrayExpression, indexExpression)

        elementReference.sql() shouldBe "array[index]"
    }

    @Test
    fun `defaultColumnName() puts default column names into SQL syntax`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, Array<Any>>> {
            whenever(it.defaultColumnName()).thenReturn("array")
        }
        val indexExpression = mock<Expression<TestableDummyEngine, Int>> {
            whenever(it.defaultColumnName()).thenReturn("index")
        }

        val elementReference = ArrayElementReference(arrayExpression, indexExpression)

        elementReference.defaultColumnName() shouldBe "array[index]"
    }

    @Test
    fun `ArrayElementReferences with equal arguments are equal`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, Array<Any?>>>()
        val indexExpression = mock<Expression<TestableDummyEngine, Int>>()

        ArrayElementReference(arrayExpression, indexExpression) shouldBe ArrayElementReference(arrayExpression, indexExpression)
    }

    @Test
    fun `ArrayElementReferences with different arguments are not equal`() {
        val arrayExpression1 = mock<Expression<TestableDummyEngine, Array<Any?>>>()
        val indexExpression1 = mock<Expression<TestableDummyEngine, Int>>()
        val arrayExpression2 = mock<Expression<TestableDummyEngine, Array<Any?>>>()
        val indexExpression2 = mock<Expression<TestableDummyEngine, Int>>()

        ArrayElementReference(arrayExpression1, indexExpression1) shouldNotBe ArrayElementReference(arrayExpression2, indexExpression1)
        ArrayElementReference(arrayExpression1, indexExpression1) shouldNotBe ArrayElementReference(arrayExpression1, indexExpression2)
    }

    @Test
    fun `ArrayElementReferences and something else are not equal`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, Array<Any?>>>()
        val indexExpression = mock<Expression<TestableDummyEngine, Int>>()

        ArrayElementReference(arrayExpression, indexExpression) shouldNotBe Any()
    }

    @Test
    fun `Equal ArrayElementReferences have the same hash code`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, Array<Any?>>>()
        val indexExpression = mock<Expression<TestableDummyEngine, Int>>()

        ArrayElementReference(arrayExpression, indexExpression).hashCode() shouldBe ArrayElementReference(arrayExpression, indexExpression).hashCode()
    }

    @Test
    fun `ArrayElementReference can be created by brackets`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, Array<Any?>>>()
        val indexExpression = mock<Expression<TestableDummyEngine, Int>>()

        arrayExpression[indexExpression] shouldBe ArrayElementReference(arrayExpression, indexExpression)
    }
}
