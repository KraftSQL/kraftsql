package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine

class ArrayLengthSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `ArrayLengthSimulator can simulate ArayLength of an array expression`() {
        val row = mock<DataRow> {
            whenever(it["array"]).thenReturn(arrayOf("a", "b", "c"))
        }
        val arrayExpression = mock<Expression<DummyEngine, Array<*>?>>().also {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> row["array"] as Array<*>? }
        }

        val simulation = context(subexpressionCallbacks) {
            ArrayLengthSimulator<DummyEngine>().simulateExpression(ArrayLength(arrayExpression))
        }

        simulation(row) shouldBe 3
    }

    @Test
    fun `Simulated ArrayLength of NULL is NULL`() {
        val row = mock<DataRow> {
            whenever(it["array"]).thenReturn(null)
        }
        val arrayExpression = mock<Expression<DummyEngine, Array<*>?>>().also {
            whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> row["array"] as Array<*>? }
        }

        val simulation = context(subexpressionCallbacks) {
            ArrayLengthSimulator<DummyEngine>().simulateExpression(ArrayLength(arrayExpression))
        }

        simulation(row) shouldBe null
    }

    @Test
    fun `ArrayLengthSimulator can simulate ArayLength wrapping an aggregation`() {
        val row = mock<DataRow> {
            whenever(it["array"]).thenReturn(arrayOf("a", "b", "c"))
        }
        val arrayExpression = mock<Expression<DummyEngine, Array<*>?>>().also {
            whenever(context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) })
                .thenReturn { _ -> row["array"] as Array<*>? }
        }

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            ArrayLengthSimulator<DummyEngine>().simulateAggregation(ArrayLength(arrayExpression))
        }

        simulation(listOf(row)) shouldBe 3
    }
}
