package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class GreaterThanTest {
    @Test
    fun `SQL combines left and right expression with greater-than in parentheses`() {
        val left = mockk<Expression<TestableDummyEngine, *>> { every { sql() } returns("left") }
        val right = mockk<Expression<TestableDummyEngine, *>> { every { sql() } returns("right") }

        val greaterThan = GreaterThan(left, right)

        greaterThan.sql() shouldBe "(left)>(right)"
    }

    @Test
    fun `GreaterThan with equal arguments is equal`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        GreaterThan(left, right) shouldBeEqual GreaterThan(left, right)
    }

    @Test
    fun `GreaterThan with different arguments is not equal`() {
        val expression1 = mockk<Expression<TestableDummyEngine, *>>()
        val expression2 = mockk<Expression<TestableDummyEngine, *>>()
        val expression3 = mockk<Expression<TestableDummyEngine, *>>()

        GreaterThan(expression1, expression2) shouldNotBeEqual GreaterThan(expression2, expression3)
    }

    @Test
    fun `GreaterThan and something else are not equal`() {
        GreaterThan<TestableDummyEngine>(mockk(), mockk()) shouldNotBeEqual Any()
    }

    @Test
    fun `GreaterThan with equal arguments have same hash code`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        GreaterThan(left, right).hashCode() shouldBeEqual GreaterThan(left, right).hashCode()
    }

    @Test
    fun `greaterThan infix function creates GreaterThan expression`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        val result = left greaterThan right

        result shouldBeEqual GreaterThan(left, right)
    }
}
