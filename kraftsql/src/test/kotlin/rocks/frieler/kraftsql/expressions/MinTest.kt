package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class MinTest {
    @Test
    fun `SQL calls MIN with expression`() {
        val expression = mock<Expression<TestableDummyEngine, Int?>> {
            whenever(it.sql()).thenReturn("x")
        }

        val sql = Min(expression).sql()

        sql shouldBe "MIN(x)"
    }

    @Test
    fun `default column name renders MIN() with argument's default column name`() {
        val expression = mock<Expression<TestableDummyEngine, Int?>> {
            whenever(it.defaultColumnName()).thenReturn("x")
        }

        val sql = Min(expression).defaultColumnName()

        sql shouldBe "MIN(${expression.defaultColumnName()})"
    }

    @Test
    fun `Min with equal argument is equal`() {
        val expression = mock<Expression<TestableDummyEngine, Int?>>()

        Min(expression) shouldBeEqual Min(expression)
    }

    @Test
    fun `Min with different argument is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Int?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Int?>>()

        Min(expression1) shouldNotBeEqual Min(expression2)
    }

    @Test
    fun `Min and something else are not equal`() {
        Min<TestableDummyEngine, Int>(mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `Min with equal arguments have same hash code`() {
        val expression = mock<Expression<TestableDummyEngine, Int?>>()

        Min(expression).hashCode() shouldBeEqual Min(expression).hashCode()
    }
}
