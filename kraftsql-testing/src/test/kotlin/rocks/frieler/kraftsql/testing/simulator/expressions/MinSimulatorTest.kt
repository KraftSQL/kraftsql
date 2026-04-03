package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Min
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import java.sql.SQLException

class MinSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    private val minSimulator = MinSimulator<DummyEngine, Comparable<Comparable<*>>>()

    @Test
    fun `MinSimulator rejects simulation as Expression`() {
        shouldThrow<SQLException> {
            context(subexpressionCallbacks) { minSimulator.simulateExpression(Min(mock())) }
        }
    }

    @Test
    fun `MinSimulator can simulate Min aggregation`() {
        val expression = mock<Expression<DummyEngine, Int>>()
        val row1 = mock<DataRow>()
        val row2 = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expression)).thenReturn { row -> if (row == row1) 1 else if (row == row2) 2 else error("unexpected row") }
        @Suppress("UNCHECKED_CAST") val min = Min(expression) as Min<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            minSimulator.simulateAggregation(min)
        }
        val result = simulation(listOf(row1, row2))

        result shouldBe 1
    }

    @Test
    fun `MinSimulator ignores NULL values`() {
        val expression = mock<Expression<DummyEngine, Int?>>()
        val rowWithValue = mock<DataRow>()
        val rowWithNull = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expression)).thenReturn { row -> if (row == rowWithValue) 1 else null }
        @Suppress("UNCHECKED_CAST") val min = Min(expression) as Min<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            minSimulator.simulateAggregation(min)
        }
        val result = simulation(listOf(rowWithValue, rowWithNull))

        result shouldBe 1
    }

    @Test
    fun `MinSimulator returns NULL as minimum of only NULL values`() {
        val expression = mock<Expression<DummyEngine, Int?>>()
        whenever(subexpressionCallbacks.simulateExpression(expression)).thenReturn { _ -> null }
        val row1 = mock<DataRow>()
        val row2 = mock<DataRow>()
        @Suppress("UNCHECKED_CAST") val min = Min(expression) as Min<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            minSimulator.simulateAggregation(min)
        }
        val result = simulation(listOf(row1, row2))

        result shouldBe null
    }

    @Test
    fun `MinSimulator returns NULL as minimum of empty data`() {
        val expression = mock<Expression<DummyEngine, Int?>>()
        whenever(subexpressionCallbacks.simulateExpression(expression)).thenReturn { _ -> null }
        @Suppress("UNCHECKED_CAST") val min = Min(expression) as Min<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            minSimulator.simulateAggregation(min)
        }
        val result = simulation(emptyList())

        result shouldBe null
    }
}
