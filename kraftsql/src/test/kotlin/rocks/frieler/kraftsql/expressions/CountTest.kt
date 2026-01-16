package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class CountTest {
    @Test
    fun `SQL calls COUNT with asterisk to count all rows`() {
        val sql = Count().sql()

        sql shouldBe "COUNT(*)"
    }

    @Test
    fun `SQL calls COUNT with expression to count non-NULL values`() {
        val expression = mock<Expression<TestableDummyEngine, *>> {
            whenever(it.sql()).thenReturn("x")
        }

        val sql = Count(expression).sql()

        sql shouldBe "COUNT(x)"
    }

    @Test
    fun `default column name renders COUNT() with asterisk when counting all rows`() {
        val sql = Count().defaultColumnName()

        sql shouldBe "COUNT(*)"
    }

    @Test
    fun `default column name renders COUNT() with argument's default column name`() {
        val expression = mock<Expression<TestableDummyEngine, Any?>> { whenever(it.defaultColumnName()).thenReturn("x") }

        val sql = Count(expression).defaultColumnName()

        sql shouldBe "COUNT(${expression.defaultColumnName()})"
    }

    @Test
    fun `Count with equal argument is equal`() {
        val expression = mock<Expression<TestableDummyEngine, Any?>>()

        Count(expression) shouldBeEqual Count(expression)
    }

    @Test
    fun `Count with different argument is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Any?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Any?>>()

        Count(expression1) shouldNotBeEqual Count(expression2)
    }

    @Test
    fun `Count and something else are not equal`() {
        Count<TestableDummyEngine>(mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `Count with equal arguments have same hash code`() {
        val expression = mock<Expression<TestableDummyEngine, Any?>>()

        Count(expression).hashCode() shouldBeEqual Count(expression).hashCode()
    }
}
