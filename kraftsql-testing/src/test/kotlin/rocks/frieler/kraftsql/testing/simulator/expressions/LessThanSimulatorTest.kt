package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.LessThan
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState

class LessThanSimulatorTest {
    private val comparator = mockk<ConvertingComparator>()
    private val simulator = LessThanSimulator<DummyEngine>(comparator)

    private val state = mockk<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mockk<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `LessThanSimulator can simulate LessThan expression`() {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(-1)
        val expression1 = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value1 }
        }
        val expression2 = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value2 }
        }
        val lessThan = LessThan(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks) {
            simulator.simulateExpression(lessThan)
        }
        val result = simulation(mockk())

        result shouldBe true
    }

    @Test
    fun `LessThanSimulator simulates LessThan expression as null when comparing null`() {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(null)
        val expression1 = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value1 }
        }
        val expression2 = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value2 }
        }
        val lessThan = LessThan(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks) {
            simulator.simulateExpression(lessThan)
        }
        val result = simulation(mockk())

        result shouldBe null
    }

    @Test
    fun `LessThanSimulator can simulate LessThan expression of aggregations`() {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(-1)
        val expression1 = mockk<Expression<DummyEngine, *>>().also {
            every { context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> value1 }
        }
        val expression2 = mockk<Expression<DummyEngine, *>>().also {
            every { context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> value2 }
        }
        val lessThan = LessThan(expression1, expression2)

        val simulation = context(state, subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            simulator.simulateAggregation(lessThan)
        }
        val result = simulation(listOf(mockk()))

        result shouldBe true
    }
}
