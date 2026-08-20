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

class MultiplicationTest {
    @Test
    fun `SQL combines left and right expression in parentheses with an asterisk`() {
        val left = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("right") }

        val multiplication = Multiplication(left, right)

        multiplication.sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `Multiplication with equal arguments is equal`() {
        val left = mock<Expression<TestableDummyEngine, Long?>>()
        val right = mock<Expression<TestableDummyEngine, Long?>>()

        Multiplication(left, right) shouldBeEqual Multiplication(left, right)
    }

    @Test
    fun `Multiplication with different arguments is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Long?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Long?>>()
        val expression3 = mock<Expression<TestableDummyEngine, Long?>>()

        Multiplication(expression1, expression2) shouldNotBeEqual Multiplication(expression2, expression3)
    }

    @Test
    fun `Multiplication and something else are not equal`() {
        Multiplication(mock<Expression<TestableDummyEngine, Long?>>(), mock<Expression<TestableDummyEngine, Long?>>()) shouldNotBeEqual Any()
    }

    @Test
    fun `Multiplication with equal arguments have same hash code`() {
        val left = mock<Expression<TestableDummyEngine, Long?>>()
        val right = mock<Expression<TestableDummyEngine, Long?>>()

        Multiplication(left, right).hashCode() shouldBeEqual Multiplication(left, right).hashCode()
    }

    @Test
    fun `invoke and times-operator support Int-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Int?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Int?>> { whenever(it.sql()).thenReturn("right") }

        Multiplication(left, right).sql() shouldBe "(left)*(right)"
        (left * right).sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke and times-operator support not-nullable Int-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Int>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Int>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Int> = Multiplication(left, right)
        val multiplied: Expression<TestableDummyEngine, Int> = left * right

        invoked.sql() shouldBe "(left)*(right)"
        multiplied.sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke and times-operator support Long-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("right") }

        Multiplication(left, right).sql() shouldBe "(left)*(right)"
        (left * right).sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke for not-nullable Long expressions yields a not-nullable result`() {
        val left = mock<Expression<TestableDummyEngine, Long>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Long> = Multiplication(left, right)
        val multiplied: Expression<TestableDummyEngine, Long> = left * right

        invoked.sql() shouldBe "(left)*(right)"
        multiplied.sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke and times-operator support BigInteger-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigInteger?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigInteger?>> { whenever(it.sql()).thenReturn("right") }

        Multiplication(left, right).sql() shouldBe "(left)*(right)"
        (left * right).sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke and times-operator support not-nullable BigInteger-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigInteger>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigInteger>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, BigInteger> = Multiplication(left, right)
        val multiplied: Expression<TestableDummyEngine, BigInteger> = left * right

        invoked.sql() shouldBe "(left)*(right)"
        multiplied.sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke and times-operator support Double-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Double?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Double?>> { whenever(it.sql()).thenReturn("right") }

        Multiplication(left, right).sql() shouldBe "(left)*(right)"
        (left * right).sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke and times-operator support not-nullable Double-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Double>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Double>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Double> = Multiplication(left, right)
        val multiplied: Expression<TestableDummyEngine, Double> = left * right

        invoked.sql() shouldBe "(left)*(right)"
        multiplied.sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke and times-operator support BigDecimal-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigDecimal?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigDecimal?>> { whenever(it.sql()).thenReturn("right") }

        Multiplication(left, right).sql() shouldBe "(left)*(right)"
        (left * right).sql() shouldBe "(left)*(right)"
    }

    @Test
    fun `invoke and times-operator support not-nullable BigDecimal-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigDecimal>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigDecimal>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, BigDecimal> = Multiplication(left, right)
        val multiplied: Expression<TestableDummyEngine, BigDecimal> = left * right

        invoked.sql() shouldBe "(left)*(right)"
        multiplied.sql() shouldBe "(left)*(right)"
    }
}
