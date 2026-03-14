package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.LessOrEqual

class LessOrEqualSimulatorTest {
    private val comparator = mock<ConvertingComparator>()
    private val simulator = LessOrEqualSimulator<DummyEngine>(comparator)

    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `LessOrEqualSimulator can simulate LessOrEqual expression`() {
        val value1 = mock<Any>()
        val value2 = mock<Any>()
        whenever(comparator.compare(value1, value2)).thenReturn(-1)
        val expression1 = mock<Expression<DummyEngine, *>> {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> value1 }
        }
        val expression2 = mock<Expression<DummyEngine, *>> {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> value2 }
        }
        val lessOrEqual = LessOrEqual(expression1, expression2)

        val simulation = context(subexpressionCallbacks) {
            simulator.simulateExpression(lessOrEqual)
        }
        val result = simulation(mock())

        result shouldBe true
    }

    @Test
    fun `LessOrEqualSimulator can simulate LessOrEqual expression of aggregations`() {
        val value1 = mock<Any>()
        val value2 = mock<Any>()
        whenever(comparator.compare(value1, value2)).thenReturn(-1)
        val expression1 = mock<Expression<DummyEngine, *>> {
            whenever(context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) })
                .thenReturn { _ -> value1 }
        }
        val expression2 = mock<Expression<DummyEngine, *>> {
            whenever(context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) })
                .thenReturn { _ -> value2 }
        }
        val lessOrEqual = LessOrEqual(expression1, expression2)

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            simulator.simulateAggregation(lessOrEqual)
        }
        val result = simulation(listOf(mock()))

        result shouldBe true
    }
}
