package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.objects.DataRow

class RowSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `RowSimulator can simulate NULL constant`() {
        val simulation = context(subexpressionCallbacks) {
            RowSimulator<DummyEngine>().simulateExpression(Row(null))
        }
        val result = simulation(mock())

        result shouldBe null
    }

    @Test
    fun `RowSimulator can simulate Row() of multiple expressions`() {
        val simulation = context(subexpressionCallbacks) {
            RowSimulator<DummyEngine>().simulateExpression(Row(mapOf(
                "key" to mock { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 1 }},
                "value" to mock { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> "foo" }},
            )))
        }
        val result = simulation(mock<DataRow>())

        result shouldBe DataRow("key" to 1, "value" to "foo")
    }

    @Test
    fun `RowSimulator can simulate Row() expression of multiple aggregations as aggregation`() {
        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            RowSimulator<DummyEngine>().simulateAggregation(Row(mapOf(
                "key" to mock { whenever(subexpressionCallbacks.simulateAggregation(it)).thenReturn { _ -> 1 }},
                "value" to mock { whenever(subexpressionCallbacks.simulateAggregation(it)).thenReturn { _ -> "foo" }},
            )))
        }
        val result = simulation(listOf(mock<DataRow>()))

        result shouldBe DataRow("key" to 1, "value" to "foo")
    }
}
