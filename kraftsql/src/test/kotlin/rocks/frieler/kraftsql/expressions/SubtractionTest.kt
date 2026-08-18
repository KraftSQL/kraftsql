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

class SubtractionTest {
    @Test
    fun `SQL combines left and right expression in parentheses with a minus`() {
        val left = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("right") }

        val subtraction = Subtraction(left, right)

        subtraction.sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `Subtraction with equal arguments is equal`() {
        val left = mock<Expression<TestableDummyEngine, Long?>>()
        val right = mock<Expression<TestableDummyEngine, Long?>>()

        Subtraction(left, right) shouldBeEqual Subtraction(left, right)
    }

    @Test
    fun `Subtraction with different arguments is not equal`() {
        val expression1 = mock<Expression<TestableDummyEngine, Long?>>()
        val expression2 = mock<Expression<TestableDummyEngine, Long?>>()
        val expression3 = mock<Expression<TestableDummyEngine, Long?>>()

        Subtraction(expression1, expression2) shouldNotBeEqual Subtraction(expression2, expression3)
    }

    @Test
    fun `Subtraction and something else are not equal`() {
        Subtraction(mock<Expression<TestableDummyEngine, Long?>>(), mock<Expression<TestableDummyEngine, Long?>>()) shouldNotBeEqual Any()
    }

    @Test
    fun `Subtraction with equal arguments have same hash code`() {
        val left = mock<Expression<TestableDummyEngine, Long?>>()
        val right = mock<Expression<TestableDummyEngine, Long?>>()

        Subtraction(left, right).hashCode() shouldBeEqual Subtraction(left, right).hashCode()
    }

    @Test
    fun `invoke and minus-operator support Int-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Int?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Int?>> { whenever(it.sql()).thenReturn("right") }

        Subtraction(left, right).sql() shouldBe "(left)-(right)"
        (left - right).sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke and minus-operator support not-nullable Int-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Int>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Int>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Int> = Subtraction(left, right)
        val minussed: Expression<TestableDummyEngine, Int> = left - right

        invoked.sql() shouldBe "(left)-(right)"
        minussed.sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke and minus-operator support Long-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long?>> { whenever(it.sql()).thenReturn("right") }

        Subtraction(left, right).sql() shouldBe "(left)-(right)"
        (left - right).sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke for not-nullable Long expressions yields a not-nullable result`() {
        val left = mock<Expression<TestableDummyEngine, Long>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Long>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Long> = Subtraction(left, right)
        val minussed: Expression<TestableDummyEngine, Long> = left - right

        invoked.sql() shouldBe "(left)-(right)"
        minussed.sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke and minus-operator support BigInteger-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigInteger?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigInteger?>> { whenever(it.sql()).thenReturn("right") }

        Subtraction(left, right).sql() shouldBe "(left)-(right)"
        (left - right).sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke and minus-operator support not-nullable BigInteger-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigInteger>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigInteger>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, BigInteger> = Subtraction(left, right)
        val minussed: Expression<TestableDummyEngine, BigInteger> = left - right

        invoked.sql() shouldBe "(left)-(right)"
        minussed.sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke and minus-operator support Double-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Double?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Double?>> { whenever(it.sql()).thenReturn("right") }

        Subtraction(left, right).sql() shouldBe "(left)-(right)"
        (left - right).sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke and minus-operator support not-nullable Double-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, Double>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, Double>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, Double> = Subtraction(left, right)
        val minussed: Expression<TestableDummyEngine, Double> = left - right

        invoked.sql() shouldBe "(left)-(right)"
        minussed.sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke and minus-operator support BigDecimal-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigDecimal?>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigDecimal?>> { whenever(it.sql()).thenReturn("right") }

        Subtraction(left, right).sql() shouldBe "(left)-(right)"
        (left - right).sql() shouldBe "(left)-(right)"
    }

    @Test
    fun `invoke and minus-operator support not-nullable BigDecimal-valued expressions`() {
        val left = mock<Expression<TestableDummyEngine, BigDecimal>> { whenever(it.sql()).thenReturn("left") }
        val right = mock<Expression<TestableDummyEngine, BigDecimal>> { whenever(it.sql()).thenReturn("right") }

        val invoked: Expression<TestableDummyEngine, BigDecimal> = Subtraction(left, right)
        val minussed: Expression<TestableDummyEngine, BigDecimal> = left - right

        invoked.sql() shouldBe "(left)-(right)"
        minussed.sql() shouldBe "(left)-(right)"
    }
}
