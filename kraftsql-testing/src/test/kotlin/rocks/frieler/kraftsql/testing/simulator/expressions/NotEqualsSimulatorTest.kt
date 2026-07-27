package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.NotEquals
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState

class NotEqualsSimulatorTest {
    private val comparator = mockk<ConvertingComparator>()
    private val simulator = NotEqualsSimulator<DummyEngine>(comparator)

    private val state = mockk<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mockk<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(
        "-1, true",
        "0, false",
        "1, true",
        "NULL, false",
        nullValues = ["NULL"]
    )
    fun `NotEqualSimulator can simulate NotEquals expression`(comparisonResult: Int?, expectedNonEquality: Boolean) {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(comparisonResult)
        val left = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value1 }
        }
        val right = mockk<Expression<DummyEngine, *>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value2 }
        }
        val notEquals = NotEquals(left, right)

        val simulation = context(state, subexpressionCallbacks) {
            simulator.simulateExpression(notEquals)
        }
        val result = simulation(mockk())

        result shouldBe expectedNonEquality
    }

    @Test
    fun `NotEqualSimulator can simulate NotEquals expression of aggregations`() {
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        every { comparator.compare(value1, value2) } returns(0)
        val left = mockk<Expression<DummyEngine, *>>().also {
            every { context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> value1 }
        }
        val right = mockk<Expression<DummyEngine, *>>().also {
            every { context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> value2 }
        }
        val notEquals = NotEquals(left, right)

        val simulation = context(state, subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            simulator.simulateAggregation(notEquals)
        }
        val result = simulation(listOf(mockk()))

        result shouldBe false
    }
}
