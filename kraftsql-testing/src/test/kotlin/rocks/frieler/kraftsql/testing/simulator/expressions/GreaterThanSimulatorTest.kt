package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.GreaterThan
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState

class GreaterThanSimulatorTest {
    private val comparator = mockk<ConvertingComparator>()
    private val simulator = GreaterThanSimulator<DummyEngine>(comparator)

    private val state = mockk<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mockk<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `GreaterThanSimulator can simulate GreaterThan expression`() {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(1)
        val expression1 = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value1 }
        }
        val expression2 = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value2 }
        }
        val greaterThan = GreaterThan(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks) {
            simulator.simulateExpression(greaterThan)
        }
        val result = simulation(mockk())

        result shouldBe true
    }

    @Test
    fun `GreaterThanSimulator simulates GreaterThan expression as null when comparing null`() {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(null)
        val expression1 = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value1 }
        }
        val expression2 = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value2 }
        }
        val greaterThan = GreaterThan(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks) {
            simulator.simulateExpression(greaterThan)
        }
        val result = simulation(mockk())

        result shouldBe null
    }

    @Test
    fun `GreaterThanSimulator can simulate GreaterThan expression of aggregations`() {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(1)
        val expression1 = mockk<Expression<DummyEngine, *>>().also {
            every { context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> value1 }
        }
        val expression2 = mockk<Expression<DummyEngine, *>>().also {
            every { context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> value2 }
        }
        val greaterThan = GreaterThan(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            simulator.simulateAggregation(greaterThan)
        }
        val result = simulation(listOf(mockk()))

        result shouldBe true
    }
}
