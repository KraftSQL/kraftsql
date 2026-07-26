package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState

class EqualsSimulatorTest {
    private val comparator = mockk<ConvertingComparator>()
    private val simulator = EqualsSimulator<DummyEngine>(comparator)

    private val state = mockk<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mockk<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(
        "-1, false",
        "0, true",
        "1, false",
        "NULL, false",
        nullValues = ["NULL"]
    )
    fun `EqualSimulator can simulate Equals expression`(comparisonResult: Int?, expectedEquality: Boolean) {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(comparisonResult)
        val left = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value1 }
        }
        val right = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value2 }
        }
        val equals = Equals(left, right)

        val simulation = context(state, subexpressionCallbacks) {
            simulator.simulateExpression(equals)
        }
        val result = simulation(mockk())

        result shouldBe expectedEquality
    }

    @Test
    fun `EqualSimulator can simulate Equals expression of aggregations`() {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(0)
        val left = mockk<Expression<DummyEngine, *>>().also {
            every { context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> value1 }
        }
        val right = mockk<Expression<DummyEngine, *>>().also {
            every { context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> value2 }
        }
        val equals = Equals(left, right)

        val simulation = context(state, subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            simulator.simulateAggregation(equals)
        }
        val result = simulation(listOf(mockk()))

        result shouldBe true
    }
}
