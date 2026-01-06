package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import java.sql.SQLException

class CountSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `CountSimulator rejects simulation as Expression`() {
        shouldThrow<SQLException> {
            context(subexpressionCallbacks) {
                CountSimulator<DummyEngine>().simulateExpression(mock())
            }
        }
    }

    @Test
    fun `CountSimulator can simulate Count as aggregation over all rows`() {
        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            CountSimulator<DummyEngine>().simulateAggregation(Count())
        }
        val result = simulation(listOf(mock(), mock()))

        result shouldBe 2L
    }

    @Test
    fun `CountSimulator can simulate Count as aggregation over non-NULL values`() {
        val expression = mock<Expression<DummyEngine, Any?>> {
            val expressionSimulation = mock<Function1<DataRow, Any?>> {
                whenever(it.invoke(any())).thenReturn(Any(), null)
            }
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn(expressionSimulation)
        }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            CountSimulator<DummyEngine>().simulateAggregation(Count(expression))
        }
        val result = simulation(listOf(mock(), mock()))

        result shouldBe 1L
    }
}
