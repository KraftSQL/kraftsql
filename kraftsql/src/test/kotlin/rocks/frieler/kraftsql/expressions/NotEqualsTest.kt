package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class NotEqualsTest {
    @Test
    fun `SQL combines left and right expression with not-equals in parentheses`() {
        val left = mockk<Expression<TestableDummyEngine, *>> { every { sql() } returns("left") }
        val right = mockk<Expression<TestableDummyEngine, *>> { every { sql() } returns("right") }

        val notEquals = NotEquals(left, right)

        notEquals.sql() shouldBe "(left)!=(right)"
    }

    @Test
    fun `NotEquals with equal arguments is equal`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        NotEquals(left, right) shouldBeEqual NotEquals(left, right)
    }

    @Test
    fun `NotEquals with different arguments is not equal`() {
        val expression1 = mockk<Expression<TestableDummyEngine, *>>()
        val expression2 = mockk<Expression<TestableDummyEngine, *>>()
        val expression3 = mockk<Expression<TestableDummyEngine, *>>()

        NotEquals(expression1, expression2) shouldNotBeEqual NotEquals(expression2, expression3)
    }

    @Test
    fun `NotEquals and something else are not equal`() {
        NotEquals<TestableDummyEngine>(mockk(), mockk()) shouldNotBeEqual Any()
    }

    @Test
    fun `NotEquals with equal arguments have same hash code`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        NotEquals(left, right).hashCode() shouldBeEqual NotEquals(left, right).hashCode()
    }

    @Test
    fun `NotEquals infix function creates NotEquals expression`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        val result = left `!=` right

        result shouldBeEqual NotEquals(left, right)
    }
}
