package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class IsNullTest {
    @Test
    fun `IsNull renders SQL for expression 'IS NULL'`() {
        val expression = mockk<Expression<TestableDummyEngine, *>> {
            every { sql() } returns "e"
        }

        IsNull(expression).sql() shouldBe "e IS NULL"
    }

    @Test
    fun `IsNull with equal argument is equal`() {
        val expression = mockk<Expression<TestableDummyEngine, *>>()

        IsNull(expression) shouldBeEqual IsNull(expression)
    }

    @Test
    fun `IsNull with different argument is not equal`() {
        val expression1 = mockk<Expression<TestableDummyEngine, *>>()
        val expression2 = mockk<Expression<TestableDummyEngine, *>>()

        IsNull(expression1) shouldNotBeEqual IsNull(expression2)
    }

    @Test
    fun `IsNull and something else are not equal`() {
        IsNull<TestableDummyEngine>(mockk()) shouldNotBeEqual Any()
    }

    @Test
    fun `IsNull with equal argument have same hash code`() {
        val expression = mockk<Expression<TestableDummyEngine, *>>()

        IsNull(expression).hashCode() shouldBeEqual IsNull(expression).hashCode()
    }
}
