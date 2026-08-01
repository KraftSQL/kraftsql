package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class IsNotNullTest {
    @Test
    fun `IsNotNull renders SQL for expression 'IS NOT NULL'`() {
        val expression = mockk<Expression<TestableDummyEngine, *>> {
            every { sql() } returns "e"
        }

        IsNotNull(expression).sql() shouldBe "e IS NOT NULL"
    }

    @Test
    fun `IsNotNull with equal argument is equal`() {
        val expression = mockk<Expression<TestableDummyEngine, *>>()

        IsNotNull(expression) shouldBeEqual IsNotNull(expression)
    }

    @Test
    fun `IsNotNull with different argument is not equal`() {
        val expression1 = mockk<Expression<TestableDummyEngine, *>>()
        val expression2 = mockk<Expression<TestableDummyEngine, *>>()

        IsNotNull(expression1) shouldNotBeEqual IsNotNull(expression2)
    }

    @Test
    fun `IsNotNull and something else are not equal`() {
        IsNotNull<TestableDummyEngine>(mockk()) shouldNotBeEqual Any()
    }

    @Test
    fun `IsNotNull with equal argument have same hash code`() {
        val expression = mockk<Expression<TestableDummyEngine, *>>()

        IsNotNull(expression).hashCode() shouldBeEqual IsNotNull(expression).hashCode()
    }
}
