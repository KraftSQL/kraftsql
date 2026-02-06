package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class AndTest {
    @Test
    fun `SQL combines left AND right expression`() {
        val left = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.sql()).thenReturn("right") }

        val and = And(left, right)

        and.sql() shouldBe "left AND right"
    }

    @Test
    fun `default column name is constructed from left and right`() {
        val left = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.defaultColumnName()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.defaultColumnName()).thenReturn("right") }

        val and = And(left, right)

        and.defaultColumnName() shouldBe "left_AND_right"
    }

    @Test
    fun `And with equal arguments is equal`() {
        val left = mock<Expression<TestableDummyEngine, Boolean?>>()
        val right = mock<Expression<TestableDummyEngine, Boolean?>>()

        And(left, right) shouldBeEqual And(left, right)
    }

    @Test
    fun `And with different arguments is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Boolean?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Boolean?>>()
        val expression3 = mock<Expression<TestableDummyEngine, Boolean?>>()

        And(expression1, expression2) shouldNotBeEqual And(expression2, expression3)
    }

    @Test
    fun `And and something else are not equal`() {
        And<TestableDummyEngine>(mock(), mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `And with equal arguments have same hash code`() {
        val left = mock<Expression<TestableDummyEngine, Boolean?>>()
        val right = mock<Expression<TestableDummyEngine, Boolean?>>()

        And(left, right).hashCode() shouldBeEqual And(left, right).hashCode()
    }
}
