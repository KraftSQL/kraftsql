package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Subtraction
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import java.math.BigDecimal
import java.math.BigInteger

class SubtractionSimulatorTest {
    private val state = mock<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `SubtractionSimulator returns NULL if either side is NULL`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> null } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 42 } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }
        val result = simulation(row)

        result shouldBe null
    }

    @Test
    fun `SubtractionSimulator can simulate subtraction of two Ints`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 5 } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3 } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }
        val result = simulation(row)

        result shouldBe 2
    }

    @Test
    fun `SubtractionSimulator throws on Int overflow`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> Int.MIN_VALUE } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 1 } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }

        shouldThrow<ArithmeticException> { simulation(row) }
    }

    @Test
    fun `SubtractionSimulator can simulate subtraction of two Longs`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 5L } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3L } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }
        val result = simulation(row)

        result shouldBe 2L
    }

    @Test
    fun `SubtractionSimulator throws on Long overflow`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> Long.MIN_VALUE } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 1 } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }

        shouldThrow<ArithmeticException> { simulation(row) }
    }

    @Test
    fun `SubtractionSimulator can simulate subtraction of two BigIntegers`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, BigInteger?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigInteger.valueOf(5) } }
        val rightHandSide = mock<Expression<DummyEngine, BigInteger?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigInteger.valueOf(3) } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }
        val result = simulation(row)

        result shouldBe BigInteger.valueOf(2)
    }

    @Test
    fun `SubtractionSimulator can simulate subtraction of two Doubles`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Double?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 5.75 } }
        val rightHandSide = mock<Expression<DummyEngine, Double?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3.25 } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }
        val result = simulation(row)

        result shouldBe 2.5
    }

    @Test
    fun `SubtractionSimulator can simulate subtraction of two BigDecimals`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, BigDecimal?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigDecimal("5.75") } }
        val rightHandSide = mock<Expression<DummyEngine, BigDecimal?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigDecimal("3.25") } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }
        val result = simulation(row)

        result shouldBe BigDecimal("2.50")
    }

    @Test
    fun `SubtractionSimulator throws for unsupported combinations of numeric types`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression<Number?>(it)).thenReturn { _ -> 2 } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3L } }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateExpression(subtraction)
        }

        shouldThrow<NotImplementedError> { simulation(row) }
    }

    @Test
    fun `SubtractionSimulator can simulate Subtraction wrapping two aggregations`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> 5L }
        }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> 3L }
        }
        @Suppress("UNCHECKED_CAST") val subtraction = Subtraction(leftHandSide, rightHandSide) as Subtraction<DummyEngine, Number>

        val simulation = context(state, groupExpressions, subexpressionCallbacks) {
            SubtractionSimulator<DummyEngine>().simulateAggregation(subtraction)
        }
        val result = simulation(listOf(row))

        result shouldBe 2L
    }
}
