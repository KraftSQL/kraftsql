package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class CoalesceTest {
    @Test
    fun `SQL calls COALESCE with arguments`() {
        val expression1 = mock<Expression<TestableDummyEngine, Any?>> { whenever(it.sql()).thenReturn("x") }
        val expression2 = mock<Expression<TestableDummyEngine, Any?>> { whenever(it.sql()).thenReturn("y") }

        val sql = Coalesce(expression1, expression2).sql()

        sql shouldMatch "COALESCE\\(${expression1.sql()},\\s*${expression2.sql()}\\)"
    }

    @Test
    fun `default column name renders COALESCE() with argument's default column names`() {
        val expression1 = mock<Expression<TestableDummyEngine, Any?>> { whenever(it.defaultColumnName()).thenReturn("x") }
        val expression2 = mock<Expression<TestableDummyEngine, Any?>> { whenever(it.defaultColumnName()).thenReturn("y") }

        val sql = Coalesce(expression1, expression2).defaultColumnName()

        sql shouldMatch "COALESCE\\(${expression1.defaultColumnName()},\\s*${expression2.defaultColumnName()}\\)"
    }

    @Test
    fun `Coalesce is non nullable when last expression is non nullable`() {
        val nullableExpressions = mock<Expression<TestableDummyEngine, Any?>>()
        val nonNullableExpression = mock<Expression<TestableDummyEngine, Any>>()

        val nonNullableCoalesce : Expression<TestableDummyEngine, Any> = Coalesce(nullableExpressions, nonNullableExpression = nonNullableExpression)

        nonNullableCoalesce shouldNotBeNull {}
    }

    @Test
    fun `Coalesce with equal arguments is equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Any?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Any?>>()

        Coalesce(expression1, expression2) shouldBeEqual Coalesce(expression1, expression2)
    }

    @Test
    fun `Coalesce with different arguments is equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Any?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Any?>>()
        val expression3 = mock<Expression<TestableDummyEngine, Any?>>()

        Coalesce(expression1, expression2) shouldNotBeEqual Coalesce(expression2, expression3)
    }

    @Test
    fun `Coalesce and something else are not equal`() {
        Coalesce<TestableDummyEngine, Any?>(mock(), mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `Coalesce with equal arguments have same hash code`() {
        val expression1 = mock<Expression<TestableDummyEngine, Any?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Any?>>()

        Coalesce(expression1, expression2).hashCode() shouldBeEqual Coalesce(expression1, expression2).hashCode()
    }
}
