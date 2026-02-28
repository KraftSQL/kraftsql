package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class LessOrEqualTest {
    @Test
    fun `SQL combines left and right expression with less-or-equal in parentheses`() {
        val left = mock<Expression<TestableDummyEngine, *>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, *>> { whenever(it.sql()).thenReturn("right") }

        val lessOrEqual = LessOrEqual(left, right)

        lessOrEqual.sql() shouldBe "(left)<=(right)"
    }

    @Test
    fun `default column name is constructed from left and right`() {
        val left = mock<Expression<TestableDummyEngine, *>> { whenever(it.defaultColumnName()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, *>> { whenever(it.defaultColumnName()).thenReturn("right") }

        val lessOrEqual = LessOrEqual(left, right)

        lessOrEqual.defaultColumnName() shouldBe "left<=right"
    }

    @Test
    fun `LessOrEqual with equal arguments is equal`() {
        val left = mock<Expression<TestableDummyEngine, *>>()
        val right = mock<Expression<TestableDummyEngine, *>>()

        LessOrEqual(left, right) shouldBeEqual LessOrEqual(left, right)
    }

    @Test
    fun `LessOrEqual with different arguments is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, *>>()
        val expression2 = mock<Expression<TestableDummyEngine, *>>()
        val expression3 = mock<Expression<TestableDummyEngine, *>>()

        LessOrEqual(expression1, expression2) shouldNotBeEqual LessOrEqual(expression2, expression3)
    }

    @Test
    fun `LessOrEqual and something else are not equal`() {
        LessOrEqual<TestableDummyEngine>(mock(), mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `LessOrEqual with equal arguments have same hash code`() {
        val left = mock<Expression<TestableDummyEngine, *>>()
        val right = mock<Expression<TestableDummyEngine, *>>()

        LessOrEqual(left, right).hashCode() shouldBeEqual LessOrEqual(left, right).hashCode()
    }

    @Test
    fun `lessOrEqual infix function creates LessOrEqual expression`() {
        val left = mock<Expression<TestableDummyEngine, *>>()
        val right = mock<Expression<TestableDummyEngine, *>>()

        val result = left lessOrEqual right

        result shouldBeEqual LessOrEqual(left, right)
    }
}
