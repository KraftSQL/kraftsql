package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class OrTest {
    @Test
    fun `SQL combines left OR right expression`() {
        val left = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.sql()).thenReturn("right") }

        val or = Or(left, right)

        or.sql() shouldBe "left OR right"
    }

    @Test
    fun `default column name is constructed from left and right`() {
        val left = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.defaultColumnName()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.defaultColumnName()).thenReturn("right") }

        val or = Or(left, right)

        or.defaultColumnName() shouldBe "left_OR_right"
    }

    @Test
    fun `Or with equal arguments is equal`() {
        val left = mock<Expression<TestableDummyEngine, Boolean?>>()
        val right = mock<Expression<TestableDummyEngine, Boolean?>>()

        Or(left, right) shouldBeEqual Or(left, right)
    }

    @Test
    fun `Or with different arguments is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Boolean?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Boolean?>>()
        val expression3 = mock<Expression<TestableDummyEngine, Boolean?>>()

        Or(expression1, expression2) shouldNotBeEqual Or(expression2, expression3)
    }

    @Test
    fun `Or and something else are not equal`() {
        Or<TestableDummyEngine>(mock(), mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `Or with equal arguments have same hash code`() {
        val left = mock<Expression<TestableDummyEngine, Boolean?>>()
        val right = mock<Expression<TestableDummyEngine, Boolean?>>()

        Or(left, right).hashCode() shouldBeEqual Or(left, right).hashCode()
    }
}
