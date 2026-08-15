package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import java.math.BigDecimal
import java.math.BigInteger

class AdditionTest {
    @Test
    fun `SQL combines left and right expression in parentheses with a plus`() {
        val left = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("right") }

        val addition = Addition(left, right)

        addition.sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `Addition with equal arguments is equal`() {
        val left = mock<Expression<TestableDummyEngine, Long?>>()
        val right = mock<Expression<TestableDummyEngine, Long?>>()

        Addition(left, right) shouldBeEqual Addition(left, right)
    }

    @Test
    fun `Addition with different arguments is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Long?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Long?>>()
        val expression3 = mock<Expression<TestableDummyEngine, Long?>>()

        Addition(expression1, expression2) shouldNotBeEqual Addition(expression2, expression3)
    }

    @Test
    fun `Addition and something else are not equal`() {
        Addition(mock<Expression<TestableDummyEngine, Long?>>(), mock<Expression<TestableDummyEngine, Long?>>()) shouldNotBeEqual Any()
    }

    @Test
    fun `Addition with equal arguments have same hash code`() {
        val left = mock<Expression<TestableDummyEngine, Long?>>()
        val right = mock<Expression<TestableDummyEngine, Long?>>()

        Addition(left, right).hashCode() shouldBeEqual Addition(left, right).hashCode()
    }

    @Test
    fun `invoke and plus-operator support Int-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Int?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Int?>> { whenever(it.sql()).thenReturn("right") }

        Addition(left, right).sql() shouldBe "(left)+(right)"
        (left + right).sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke and plus-operator support not-nullable Int-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Int>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Int>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Int> = Addition(left, right)
        val plussed: Expression<TestableDummyEngine, Int> = left + right

        invoked.sql() shouldBe "(left)+(right)"
        plussed.sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke and plus-operator support Long-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("right") }

        Addition(left, right).sql() shouldBe "(left)+(right)"
        (left + right).sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke for not-nullable Long expressions yields a not-nullable result`() {
        val left = mock<Expression<TestableDummyEngine, Long>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Long> = Addition(left, right)
        val plussed: Expression<TestableDummyEngine, Long> = left + right

        invoked.sql() shouldBe "(left)+(right)"
        plussed.sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke and plus-operator support BigInteger-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigInteger?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigInteger?>> { whenever(it.sql()).thenReturn("right") }

        Addition(left, right).sql() shouldBe "(left)+(right)"
        (left + right).sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke and plus-operator support not-nullable BigInteger-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigInteger>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigInteger>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, BigInteger> = Addition(left, right)
        val plussed: Expression<TestableDummyEngine, BigInteger> = left + right

        invoked.sql() shouldBe "(left)+(right)"
        plussed.sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke and plus-operator support Double-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Double?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Double?>> { whenever(it.sql()).thenReturn("right") }

        Addition(left, right).sql() shouldBe "(left)+(right)"
        (left + right).sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke and plus-operator support not-nullable Double-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Double>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Double>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Double> = Addition(left, right)
        val plussed: Expression<TestableDummyEngine, Double> = left + right

        invoked.sql() shouldBe "(left)+(right)"
        plussed.sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke and plus-operator support BigDecimal-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigDecimal?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigDecimal?>> { whenever(it.sql()).thenReturn("right") }

        Addition(left, right).sql() shouldBe "(left)+(right)"
        (left + right).sql() shouldBe "(left)+(right)"
    }

    @Test
    fun `invoke and plus-operator support not-nullable BigDecimal-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigDecimal>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigDecimal>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, BigDecimal> = Addition(left, right)
        val plussed: Expression<TestableDummyEngine, BigDecimal> = left + right

        invoked.sql() shouldBe "(left)+(right)"
        plussed.sql() shouldBe "(left)+(right)"
    }
}
