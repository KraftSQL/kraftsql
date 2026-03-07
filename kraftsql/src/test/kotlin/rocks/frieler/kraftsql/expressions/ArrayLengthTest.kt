package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class ArrayLengthTest {
    @Test
    fun `SQL calls ARRAY_LENGTH with array expression`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, kotlin.Array<*>>> {
            whenever(it.sql()).thenReturn("array")
        }

        val arrayLength = ArrayLength(arrayExpression)

        arrayLength.sql() shouldBe "ARRAY_LENGTH(array)"
    }

    @Test
    fun `defaultColumnName() wraps array expression's default column name in function call`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, kotlin.Array<*>>>{
            whenever(it.defaultColumnName()).thenReturn("array")
        }

        val arrayLength = ArrayLength(arrayExpression)

        arrayLength.defaultColumnName() shouldBe "ARRAY_LENGTH(array)"
    }

    @Test
    fun `ArrayLengths with equal argument are equal`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, kotlin.Array<*>>>()

        ArrayLength(arrayExpression) shouldBe ArrayLength(arrayExpression)
    }

    @Test
    fun `ArrayLengths with different argument are not equal`() {
        val arrayExpression1 = mock<Expression<TestableDummyEngine, kotlin.Array<*>>>()
        val arrayExpression2 = mock<Expression<TestableDummyEngine, kotlin.Array<*>>>()

        ArrayLength(arrayExpression1) shouldNotBe ArrayLength(arrayExpression2)
    }

    @Test
    fun `ArrayLength and something else are not equal`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, kotlin.Array<*>>>()

        ArrayLength(arrayExpression) shouldNotBe Any()
    }

    @Test
    fun `Equal ArrayLengths have the same hash code`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, kotlin.Array<*>>>()

        ArrayLength(arrayExpression).hashCode() shouldBe ArrayLength(arrayExpression).hashCode()
    }
}
