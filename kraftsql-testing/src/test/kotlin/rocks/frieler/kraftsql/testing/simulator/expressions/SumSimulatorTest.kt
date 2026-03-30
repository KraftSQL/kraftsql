package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
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
        val expressionToSum = mock<Expression<DummyEngine, Long>>()
        val rowWithByte = mock<DataRow>()
        val rowWithShort = mock<DataRow>()
        val rowWithInt = mock<DataRow>()
        val rowWithLong = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, Number>))
            .thenReturn {row -> when(row) {
                rowWithByte -> 1.toByte()
                rowWithShort -> 2.toShort()
                rowWithInt -> 3
                rowWithLong -> 4L
                else -> error("unexpected row")
            } }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsLongSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(rowWithByte, rowWithShort, rowWithInt, rowWithLong))

        result shouldBe 10L
    }

    @Test
    fun `SumAsLongSimulator ignores NULL values`() {
        val expressionToSum = mock<Expression<DummyEngine, Long?>>()
        val rowWithValue = mock<DataRow>()
        val rowWithNull = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToSum))
            .thenReturn { row -> if (row == rowWithValue) 1L else null }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsLongSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(rowWithValue, rowWithNull))

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
        val expressionToSum = mock<Expression<DummyEngine, Double>>()
        val rowWithByte = mock<DataRow>()
        val rowWithShort = mock<DataRow>()
        val rowWithInt = mock<DataRow>()
        val rowWithLong = mock<DataRow>()
        val rowWithFloat = mock<DataRow>()
        val rowWithDouble = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToSum as Expression<DummyEngine, Number>))
            .thenReturn { row -> when (row) {
                rowWithByte -> 1.toByte()
                rowWithShort -> 2.toShort()
                rowWithInt -> 3
                rowWithLong -> 4L
                rowWithFloat -> 2.18F
                rowWithDouble -> 3.14
                else -> error("")
            } }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsDoubleSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(rowWithByte, rowWithShort, rowWithInt, rowWithLong, rowWithFloat, rowWithDouble))

        result shouldBe 15.32.plusOrMinus(0.0001)
    }

    @Test
    fun `SumAsDoubleSimulator ignores NULL values`() {
        val expressionToSum = mock<Expression<DummyEngine, Double?>>()
        val rowWithValue = mock<DataRow>()
        val rowWithNull = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToSum))
            .thenReturn { row -> if (row == rowWithValue) 1.0 else null }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsDoubleSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(rowWithValue, rowWithNull))

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
        val expressionToSum = mock<Expression<DummyEngine, BigDecimal>>()
        val row1 = mock<DataRow>()
        val row2 = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToSum))
            .thenReturn { row -> if (row == row1) BigDecimal.ONE else if (row == row2) BigDecimal.TWO else error("unexpected row") }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsBigDecimalSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(row1, row2))

        result shouldBe BigDecimal.valueOf(3)
    }

    @Test
    fun `SumAsBigDecimalSimulator ignores NULL values`() {
        val expressionToSum = mock<Expression<DummyEngine, BigDecimal?>>()
        val rowWithValue = mock<DataRow>()
        val rowWithNull = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToSum))
            .thenReturn { row -> if (row == rowWithValue) BigDecimal.ONE else null }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            SumAsBigDecimalSimulator<DummyEngine>().simulateAggregation(Sum(expressionToSum))
        }
        val result = simulation(listOf(rowWithValue, rowWithNull))

        result shouldBe BigDecimal.ONE
    }
}
