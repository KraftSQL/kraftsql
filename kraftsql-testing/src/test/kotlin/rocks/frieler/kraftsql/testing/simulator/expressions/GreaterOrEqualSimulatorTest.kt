package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.GreaterOrEqual
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState

class GreaterOrEqualSimulatorTest {
    private val comparator = mock<ConvertingComparator>()
    private val simulator = GreaterOrEqualSimulator<DummyEngine>(comparator)

    private val state = mock<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `GreaterOrEqualSimulator can simulate GreaterOrEqual expression`() {
        val value1 = mock<Any>()
        val value2 = mock<Any>()
        whenever(comparator.compare(value1, value2)).thenReturn(1)
        val expression1 = mock<Expression<DummyEngine, *>> {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> value1 }
        }
        val expression2 = mock<Expression<DummyEngine, *>> {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> value2 }
        }
        val greaterOrEqual = GreaterOrEqual(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks) {
            simulator.simulateExpression(greaterOrEqual)
        }
        val result = simulation(mock())

        result shouldBe true
    }

    @Test
    fun `GreaterOrEqualSimulator simulates GreaterOrEqual expression as null when comparing null`() {
        val value1 = mock<Any>()
        val value2 = mock<Any>()
        whenever(comparator.compare(value1, value2)).thenReturn(null)
        val expression1 = mock<Expression<DummyEngine, *>> {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> value1 }
        }
        val expression2 = mock<Expression<DummyEngine, *>> {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> value2 }
        }
        val greaterOrEqual = GreaterOrEqual(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks) {
            simulator.simulateExpression(greaterOrEqual)
        }
        val result = simulation(mock())

        result shouldBe null
    }

    @Test
    fun `GreaterOrEqualSimulator can simulate GreaterOrEqual expression of aggregations`() {
        val value1 = mock<Any>()
        val value2 = mock<Any>()
        whenever(comparator.compare(value1, value2)).thenReturn(1)
        val expression1 = mock<Expression<DummyEngine, *>> {
            whenever(context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) })
                .thenReturn { _ -> value1 }
        }
        val expression2 = mock<Expression<DummyEngine, *>> {
            whenever(context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) })
                .thenReturn { _ -> value2 }
        }
        val greaterOrEqual = GreaterOrEqual(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            simulator.simulateAggregation(greaterOrEqual)
        }
        val result = simulation(listOf(mock()))

        result shouldBe true
    }
}
