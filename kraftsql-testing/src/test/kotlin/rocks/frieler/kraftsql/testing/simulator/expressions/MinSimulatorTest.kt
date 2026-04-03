package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import io.mockk.mockk
import io.mockk.every
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Min
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import java.sql.SQLException

class MinSimulatorTest {
    private val subexpressionCallbacks = mockk<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    private val minSimulator = MinSimulator<DummyEngine, Comparable<Comparable<*>>>()

    @Test
    fun `MinSimulator rejects simulation as Expression`() {
        shouldThrow<SQLException> {
            context(subexpressionCallbacks) { minSimulator.simulateExpression(Min(mockk())) }
        }
    }

    @Test
    fun `MinSimulator can simulate Min aggregation`() {
        val expression = mockk<Expression<DummyEngine, Int>>()
        val expressionSimulation = mockk<Function1<DataRow, Int>>()
        every { subexpressionCallbacks.simulateExpression(expression) } returns expressionSimulation
        val row1 = mockk<DataRow>().also { every { expressionSimulation.invoke(it) } returns 1 }
        val row2 = mockk<DataRow>().also { every { expressionSimulation.invoke(it) } returns 2 }
        @Suppress("UNCHECKED_CAST") val min = Min(expression) as Min<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            minSimulator.simulateAggregation(min)
        }
        val result = simulation(listOf(row1, row2))

        result shouldBe 1
    }

    @Test
    fun `MinSimulator ignores NULL values`() {
        val expression = mockk<Expression<DummyEngine, Int?>>()
        val expressionSimulation = mockk<Function1<DataRow, Int?>>()
        every { subexpressionCallbacks.simulateExpression(expression) } returns expressionSimulation
        val rowWithValue = mockk<DataRow>().also { every { expressionSimulation.invoke(it) } returns 1 }
        val rowWithNull = mockk<DataRow>().also { every { expressionSimulation.invoke(it) } returns null }
        @Suppress("UNCHECKED_CAST") val min = Min(expression) as Min<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            minSimulator.simulateAggregation(min)
        }
        val result = simulation(listOf(rowWithValue, rowWithNull))

        result shouldBe 1
    }

    @Test
    fun `MinSimulator returns NULL as minimum of only NULL values`() {
        val expression = mockk<Expression<DummyEngine, Int?>>()
        val expressionSimulation = mockk<Function1<DataRow, Int?>>()
        every { subexpressionCallbacks.simulateExpression(expression) } returns expressionSimulation
        val row1 = mockk<DataRow>().also { every { expressionSimulation.invoke(it) } returns null }
        val row2 = mockk<DataRow>().also { every { expressionSimulation.invoke(it) } returns null }
        @Suppress("UNCHECKED_CAST") val min = Min(expression) as Min<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            minSimulator.simulateAggregation(min)
        }
        val result = simulation(listOf(row1, row2))

        result shouldBe null
    }

    @Test
    fun `MinSimulator returns NULL as minimum of empty data`() {
        val expression = mockk<Expression<DummyEngine, Int?>>()
        val expressionSimulation = mockk<Function1<DataRow, Int?>>()
        every { subexpressionCallbacks.simulateExpression(expression) } returns expressionSimulation
        @Suppress("UNCHECKED_CAST") val min = Min(expression) as Min<DummyEngine, Comparable<Comparable<*>>>

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            minSimulator.simulateAggregation(min)
        }
        val result = simulation(emptyList())

        result shouldBe null
    }
}
