package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class MaxTest {
    @Test
    fun `SQL calls MAX with expression`() {
        val expression = mock<Expression<TestableDummyEngine, Int?>> {
            whenever(it.sql()).thenReturn("x")
        }

        val sql = Max(expression).sql()

        sql shouldBe "MAX(x)"
    }

    @Test
    fun `default column name renders MAX() with argument's default column name`() {
        val expression = mock<Expression<TestableDummyEngine, Int?>> {
            whenever(it.defaultColumnName()).thenReturn("x")
        }

        val sql = Max(expression).defaultColumnName()

        sql shouldBe "MAX(${expression.defaultColumnName()})"
    }

    @Test
    fun `Max with equal argument is equal`() {
        val expression = mock<Expression<TestableDummyEngine, Int?>>()

        Max(expression) shouldBeEqual Max(expression)
    }

    @Test
    fun `Max with different argument is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Int?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Int?>>()

        Max(expression1) shouldNotBeEqual Max(expression2)
    }

    @Test
    fun `Max and something else are not equal`() {
        Max<TestableDummyEngine, Int>(mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `Max with equal arguments have same hash code`() {
        val expression = mock<Expression<TestableDummyEngine, Int?>>()

        Max(expression).hashCode() shouldBeEqual Max(expression).hashCode()
    }
}
