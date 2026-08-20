package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Multiplication
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import java.math.BigDecimal
import java.math.BigInteger

class MultiplicationSimulatorTest {
    private val state = mock<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `MultiplicationSimulator returns NULL if either side is NULL`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> null } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 42 } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }
        val result = simulation(row)

        result shouldBe null
    }

    @Test
    fun `MultiplicationSimulator can simulate multiplication of two Ints`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 5 } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3 } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }
        val result = simulation(row)

        result shouldBe 15
    }

    @Test
    fun `MultiplicationSimulator throws on Int overflow`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> Int.MAX_VALUE } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 2 } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }

        shouldThrow<ArithmeticException> { simulation(row) }
    }

    @Test
    fun `MultiplicationSimulator can simulate multiplication of two Longs`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 5L } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3L } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }
        val result = simulation(row)

        result shouldBe 15L
    }

    @Test
    fun `MultiplicationSimulator throws on Long overflow`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> Long.MAX_VALUE } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 2 } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }

        shouldThrow<ArithmeticException> { simulation(row) }
    }

    @Test
    fun `MultiplicationSimulator can simulate multiplication of two BigIntegers`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, BigInteger?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigInteger.valueOf(5) } }
        val rightHandSide = mock<Expression<DummyEngine, BigInteger?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigInteger.valueOf(3) } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }
        val result = simulation(row)

        result shouldBe BigInteger.valueOf(15)
    }

    @Test
    fun `MultiplicationSimulator can simulate multiplication of two Doubles`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Double?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 5.5 } }
        val rightHandSide = mock<Expression<DummyEngine, Double?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 2.0 } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }
        val result = simulation(row)

        result shouldBe 11.0
    }

    @Test
    fun `MultiplicationSimulator can simulate multiplication of two BigDecimals`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, BigDecimal?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigDecimal("5.75") } }
        val rightHandSide = mock<Expression<DummyEngine, BigDecimal?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigDecimal("3.25") } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }
        val result = simulation(row)

        result shouldBe BigDecimal("18.6875")
    }

    @Test
    fun `MultiplicationSimulator throws for unsupported combinations of numeric types`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression<Number?>(it)).thenReturn { _ -> 2 } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3L } }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateExpression(multiplication)
        }

        shouldThrow<NotImplementedError> { simulation(row) }
    }

    @Test
    fun `MultiplicationSimulator can simulate Multiplication wrapping two aggregations`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> 5L }
        }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> 3L }
        }
        @Suppress("UNCHECKED_CAST") val multiplication = Multiplication(leftHandSide, rightHandSide) as Multiplication<DummyEngine, Number>

        val simulation = context(state, groupExpressions, subexpressionCallbacks) {
            MultiplicationSimulator<DummyEngine>().simulateAggregation(multiplication)
        }
        val result = simulation(listOf(row))

        result shouldBe 15L
    }
}
