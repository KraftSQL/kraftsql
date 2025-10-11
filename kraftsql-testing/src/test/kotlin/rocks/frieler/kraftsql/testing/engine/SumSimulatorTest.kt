package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.objects.DataRow
import java.math.BigDecimal
import java.sql.SQLException

class SumSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `SumAsLongSimulator rejects simulation as Expression`() {
        shouldThrow<SQLException> {
            context(subexpressionCallbacks) {
                SumAsLongSimulator<DummyEngine>().simulateExpression(mock())
            }
        }
    }

    @Test
    fun `SumAsLongSimulator sums Long-compatible values`() {
        val expressionToSum = mock<Expression<DummyEngine, Long>>().also {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn(mock())
        }
        val rowWithByte = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(1.toByte()) }
        val rowWithShort = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(2.toShort()) }
        val rowWithInt = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(3) }
        val rowWithLong = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(4L) }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsLongSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(rowWithByte, rowWithShort, rowWithInt, rowWithLong))

        result shouldBe 10L
    }

    @Test
    fun `SumAsLongSimulator ignores NULL values`() {
        val expressionToSum = mock<Expression<DummyEngine, Long>>().also {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn(mock())
        }
        val row = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum)(it)).thenReturn(1L) }
        val rowWithNull = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum)(it)).thenReturn(null) }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsLongSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(row, rowWithNull))

        result shouldBe 1L
    }

    @Test
    fun `SumAsDoubleSimulator rejects simulation as Expression`() {
        shouldThrow<SQLException> {
            context(subexpressionCallbacks) {
                SumAsDoubleSimulator<DummyEngine>().simulateExpression(mock())
            }
        }
    }

    @Test
    fun `SumAsDoubleSimulator sums Double-compatible values`() {
        val expressionToSum = mock<Expression<DummyEngine, Double>>().also {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn(mock())
        }
        val rowWithByte = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(1.toByte()) }
        val rowWithShort = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(2.toShort()) }
        val rowWithInt = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(3) }
        val rowWithLong = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(4L) }
        val rowWithFloat = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(2.18F) }
        val rowWithDouble = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, *>)(it)).thenReturn(3.14) }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsDoubleSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(rowWithByte, rowWithShort, rowWithInt, rowWithLong, rowWithFloat, rowWithDouble))

        result shouldBe 15.32.plusOrMinus(0.0001)
    }

    @Test
    fun `SumAsDoubleSimulator ignores NULL values`() {
        val expressionToSum = mock<Expression<DummyEngine, Double>>().also {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn(mock())
        }
        val row = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum)(it)).thenReturn(1.0) }
        val rowWithNull = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum)(it)).thenReturn(null) }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsDoubleSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(row, rowWithNull))

        result shouldBe 1.0
    }

    @Test
    fun `SumAsBigDecimalSimulator rejects simulation as Expression`() {
        shouldThrow<SQLException> {
            context(subexpressionCallbacks) {
                SumAsBigDecimalSimulator<DummyEngine>().simulateExpression(mock())
            }
        }
    }

    @Test
    fun `SumAsBigDecimalSimulator sums BigDecimal values`() {
        val expressionToSum = mock<Expression<DummyEngine, BigDecimal>>().also {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn(mock())
        }
        val row1 = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum)(it)).thenReturn(BigDecimal.ONE) }
        val row2 = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum)(it)).thenReturn(BigDecimal.TWO) }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsBigDecimalSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(row1, row2))

        result shouldBe BigDecimal.valueOf(3)
    }

    @Test
    fun `SumAsBigDecimalSimulator ignores NULL values`() {
        val expressionToSum = mock<Expression<DummyEngine, BigDecimal>>().also {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn(mock())
        }
        val row = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum)(it)).thenReturn(BigDecimal.ONE) }
        val rowWithNull = mock<DataRow>().also { whenever(subexpressionCallbacks.simulateExpression(expressionToSum)(it)).thenReturn(null) }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsBigDecimalSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(row, rowWithNull))

        result shouldBe BigDecimal.ONE
    }
}
