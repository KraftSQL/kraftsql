package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Addition
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import java.math.BigDecimal
import java.math.BigInteger

class AdditionSimulatorTest {
    private val state = mock<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `AdditionSimulator returns NULL if either side is NULL`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> null } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 42 } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }
        val result = simulation(row)

        result shouldBe null
    }

    @Test
    fun `AdditionSimulator can simulate addition of two Ints`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 2 } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3 } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }
        val result = simulation(row)

        result shouldBe 5
    }

    @Test
    fun `AdditionSimulator throws on Int overflow`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> Int.MAX_VALUE } }
        val rightHandSide = mock<Expression<DummyEngine, Int?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 1 } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }

        shouldThrow<ArithmeticException> { simulation(row) }
    }

    @Test
    fun `AdditionSimulator can simulate addition of two Longs`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 2L } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3L } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }
        val result = simulation(row)

        result shouldBe 5L
    }

    @Test
    fun `AdditionSimulator throws on Long overflow`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> Long.MAX_VALUE } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 1 } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }

        shouldThrow<ArithmeticException> { simulation(row) }
    }

    @Test
    fun `AdditionSimulator can simulate addition of two BigIntegers`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, BigInteger?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigInteger.valueOf(2) } }
        val rightHandSide = mock<Expression<DummyEngine, BigInteger?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigInteger.valueOf(3) } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }
        val result = simulation(row)

        result shouldBe BigInteger.valueOf(5)
    }

    @Test
    fun `AdditionSimulator can simulate addition of two Doubles`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Double?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 2.5 } }
        val rightHandSide = mock<Expression<DummyEngine, Double?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3.25 } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }
        val result = simulation(row)

        result shouldBe 5.75
    }

    @Test
    fun `AdditionSimulator can simulate addition of two BigDecimals`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, BigDecimal?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigDecimal("2.5") } }
        val rightHandSide = mock<Expression<DummyEngine, BigDecimal?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> BigDecimal("3.25") } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }
        val result = simulation(row)

        result shouldBe BigDecimal("5.75")
    }

    @Test
    fun `AdditionSimulator throws for unsupported combinations of numeric types`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression<Number?>(it)).thenReturn { _ -> 2 } }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3L } }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateExpression(addition)
        }

        shouldThrow<NotImplementedError> { simulation(row) }
    }

    @Test
    fun `AdditionSimulator can simulate Addition wrapping two aggregations`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Long?>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> 2L }
        }
        val rightHandSide = mock<Expression<DummyEngine, Long?>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> 3L }
        }
        @Suppress("UNCHECKED_CAST") val addition = Addition(leftHandSide, rightHandSide) as Addition<DummyEngine, Number>

        val simulation = context(state, groupExpressions, subexpressionCallbacks) {
            AdditionSimulator<DummyEngine>().simulateAggregation(addition)
        }
        val result = simulation(listOf(row))

        result shouldBe 5L
    }
}
