package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Expression

class CoalesceSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()
    private val coalesceSimulator = CoalesceSimulator<DummyEngine, Any?>()

    @Test
    fun `simulated COALESCE returns first non-null value`() {
        val coalesce = mock<Coalesce<DummyEngine, Any?>> {coalesce ->
            val expression1 = mock<Expression<DummyEngine, Any?>>().also {
                whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> null }
            }
            val expression2 = mock<Expression<DummyEngine, Any?>>().also {
                whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 2 }
            }
            val expression3 = mock<Expression<DummyEngine, Any?>>().also {
                whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3 }
            }
            whenever(coalesce.expressions).thenReturn(listOf(expression1, expression2, expression3))
        }

        val simulation = context(subexpressionCallbacks) {
            coalesceSimulator.simulateExpression(coalesce)
        }

        simulation(mock()) shouldBe 2
    }

    @Test
    fun `simulated COALESCE returns null when all arguments are null`() {
        val coalesce = mock<Coalesce<DummyEngine, Any?>> {coalesce ->
            val expression1 = mock<Expression<DummyEngine, Any?>>().also {
                whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> null }
            }
            val expression2 = mock<Expression<DummyEngine, Any?>>().also {
                whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> null }
            }
            whenever(coalesce.expressions).thenReturn(listOf(expression1, expression2))
        }

        val simulation = context(subexpressionCallbacks) {
            coalesceSimulator.simulateExpression(coalesce)
        }

        simulation(mock()) shouldBe null
    }

    @Test
    fun `CoalesceSimulator can simulate Coalesce as aggregation`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val coalesce = mock<Coalesce<DummyEngine, Any?>> { coalesce ->
            val expression1 = mock<Expression<DummyEngine, Any?>>().also {
                whenever(context(groupExpressions) { subexpressionCallbacks.simulateAggregation(it) }).thenReturn { _ -> null }
            }
            val expression2 = mock<Expression<DummyEngine, Any?>>().also {
                whenever(context(groupExpressions) { subexpressionCallbacks.simulateAggregation(it) }).thenReturn { _ -> 2 }
            }
            whenever(coalesce.expressions).thenReturn(listOf(expression1, expression2))
        }

        val simulation = context(subexpressionCallbacks, groupExpressions) {
            coalesceSimulator.simulateAggregation(coalesce)
        }

        simulation(mock()) shouldBe 2
    }
}
