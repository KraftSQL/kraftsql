package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class LessThanTest {
    @Test
    fun `SQL combines left and right expression with less-than in parentheses`() {
        val left = mockk<Expression<TestableDummyEngine, *>> { every { sql() } returns("left") }
        val right = mockk<Expression<TestableDummyEngine, *>> { every { sql() } returns("right") }

        val lessThan = LessThan(left, right)

        lessThan.sql() shouldBe "(left)<(right)"
    }

    @Test
    fun `LessThan with equal arguments is equal`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        LessThan(left, right) shouldBeEqual LessThan(left, right)
    }

    @Test
    fun `LessThan with different arguments is not equal`() {
        val expression1 = mockk<Expression<TestableDummyEngine, *>>()
        val expression2 = mockk<Expression<TestableDummyEngine, *>>()
        val expression3 = mockk<Expression<TestableDummyEngine, *>>()

        LessThan(expression1, expression2) shouldNotBeEqual LessThan(expression2, expression3)
    }

    @Test
    fun `LessThan and something else are not equal`() {
        LessThan<TestableDummyEngine>(mockk(), mockk()) shouldNotBeEqual Any()
    }

    @Test
    fun `LessThan with equal arguments have same hash code`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        LessThan(left, right).hashCode() shouldBeEqual LessThan(left, right).hashCode()
    }

    @Test
    fun `lessThan infix function creates LessThan expression`() {
        val left = mockk<Expression<TestableDummyEngine, *>>()
        val right = mockk<Expression<TestableDummyEngine, *>>()

        val result = left lessThan right

        result shouldBeEqual LessThan(left, right)
    }
}
