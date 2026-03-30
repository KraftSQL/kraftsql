package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import java.sql.SQLException

class MaxSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    private val maxSimulator = MaxSimulator<DummyEngine, Comparable<Comparable<*>>>()

    @Test
    fun `MaxSimulator rejects simulation as Expression`() {
        shouldThrow<SQLException> {
            context(subexpressionCallbacks) { maxSimulator.simulateExpression(Max(mock())) }
        }
    }

    @Test
    fun `MaxSimulator can simulate Max aggregation`() {
        val expression = mock<Expression<DummyEngine, Int>>()
        val row1 = mock<DataRow>()
        val row2 = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expression)).thenReturn { row -> if (row == row1) 1 else if (row == row2) 2 else error("unexpected row") }
        @Suppress("UNCHECKED_CAST") val max = Max(expression) as Max<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            maxSimulator.simulateAggregation(max)
        }
        val result = simulation(listOf(row1, row2))

        result shouldBe 2
    }

    @Test
    fun `MaxSimulator ignores NULL values`() {
        val expression = mock<Expression<DummyEngine, Int?>>()
        val rowWithValue = mock<DataRow>()
        val rowWithNull = mock<DataRow>()
        whenever(subexpressionCallbacks.simulateExpression(expression)).thenReturn { row -> if (row == rowWithValue) 1 else null }
        @Suppress("UNCHECKED_CAST") val max = Max(expression) as Max<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            maxSimulator.simulateAggregation(max)
        }
        val result = simulation(listOf(rowWithValue, rowWithNull))

        result shouldBe 1
    }

    @Test
    fun `MaxSimulator returns NULL as maximum of only NULL values`() {
        val expression = mock<Expression<DummyEngine, Int?>>()
        whenever(subexpressionCallbacks.simulateExpression(expression)).thenReturn { _ -> null }
        val row1 = mock<DataRow>()
        val row2 = mock<DataRow>()
        @Suppress("UNCHECKED_CAST") val max = Max(expression) as Max<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            maxSimulator.simulateAggregation(max)
        }
        val result = simulation(listOf(row1, row2))

        result shouldBe null
    }

    @Test
    fun `MaxSimulator returns NULL as maximum of empty data`() {
        val expression = mock<Expression<DummyEngine, Int?>>()
        whenever(subexpressionCallbacks.simulateExpression(expression)).thenReturn { _ -> null }
        @Suppress("UNCHECKED_CAST") val max = Max(expression) as Max<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            maxSimulator.simulateAggregation(max)
        }
        val result = simulation(emptyList())

        result shouldBe null
    }
}
