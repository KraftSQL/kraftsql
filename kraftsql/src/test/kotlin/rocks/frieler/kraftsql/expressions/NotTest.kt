package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class NotTest {
    @Test
    fun `SQL prefixes expression with NOT`() {
        val expression = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.sql()).thenReturn("expr") }

        val not = Not(expression)

        not.sql() shouldBe "NOT (expr)"
    }

    @Test
    fun `default column name is constructed from expression`() {
        val expression = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.defaultColumnName()).thenReturn("expr") }

        val not = Not(expression)

        not.defaultColumnName() shouldBe "NOT_expr"
    }

    @Test
    fun `Not with equal argument is equal`() {
        val expression = mock<Expression<TestableDummyEngine, Boolean?>>()

        Not(expression) shouldBeEqual Not(expression)
    }

    @Test
    fun `Not with different arguments is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Boolean?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Boolean?>>()

        Not(expression1) shouldNotBeEqual Not(expression2)
    }

    @Test
    fun `Not and something else are not equal`() {
        Not<TestableDummyEngine>(mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `Not with equal argument has same hash code`() {
        val expression = mock<Expression<TestableDummyEngine, Boolean?>>()

        Not(expression).hashCode() shouldBeEqual Not(expression).hashCode()
    }
}
