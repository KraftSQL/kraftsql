package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class GreaterOrEqualTest {
    @Test
    fun `SQL combines left and right expression with greater-or-equal in parentheses`() {
        val left = mock<Expression<TestableDummyEngine, *>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, *>> { whenever(it.sql()).thenReturn("right") }

        val greaterOrEqual = GreaterOrEqual(left, right)

        greaterOrEqual.sql() shouldBe "(left)>=(right)"
    }

    @Test
    fun `GreaterOrEqual with equal arguments is equal`() {
        val left = mock<Expression<TestableDummyEngine, *>>()
        val right = mock<Expression<TestableDummyEngine, *>>()

        GreaterOrEqual(left, right) shouldBeEqual GreaterOrEqual(left, right)
    }

    @Test
    fun `GreaterOrEqual with different arguments is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, *>>()
        val expression2 = mock<Expression<TestableDummyEngine, *>>()
        val expression3 = mock<Expression<TestableDummyEngine, *>>()

        GreaterOrEqual(expression1, expression2) shouldNotBeEqual GreaterOrEqual(expression2, expression3)
    }

    @Test
    fun `GreaterOrEqual and something else are not equal`() {
        GreaterOrEqual<TestableDummyEngine>(mock(), mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `GreaterOrEqual with equal arguments have same hash code`() {
        val left = mock<Expression<TestableDummyEngine, *>>()
        val right = mock<Expression<TestableDummyEngine, *>>()

        GreaterOrEqual(left, right).hashCode() shouldBeEqual GreaterOrEqual(left, right).hashCode()
    }

    @Test
    fun `greaterOrEqual infix function creates GreaterOrEqual expression`() {
        val left = mock<Expression<TestableDummyEngine, *>>()
        val right = mock<Expression<TestableDummyEngine, *>>()

        val result = left greaterOrEqual right

        result shouldBeEqual GreaterOrEqual(left, right)
    }
}
