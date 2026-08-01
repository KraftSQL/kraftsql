package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNull
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState

class IsNullSimulatorTest {
    private val state = mockk<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mockk<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(
        "'foo', false",
        "NULL, true",
        nullValues = ["NULL"]
    )
    fun `IsNullSimulator can simulate IS NULL operator`(value: String?, isNull: Boolean) {
        val row = mockk<DataRow>()
        val expression = mockk<Expression<DummyEngine, String?>>().also {
            every { subexpressionCallbacks.simulateExpression(it) } returns { _ -> value }
        }

        val simulation = context(state, subexpressionCallbacks) {
            IsNullSimulator<DummyEngine>().simulateExpression(IsNull(expression))
        }
        val result = simulation(row)

        result shouldBe isNull
    }


    @Test
    fun `IsNullSimulator can simulate IS NULL wrapping an aggregation`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mockk<DataRow>()
        val expression = mockk<Expression<DummyEngine, String?>>().also {
            every { context(groupExpressions) { subexpressionCallbacks.simulateAggregation(it) } } returns { _ -> "foo" }
        }

        val simulation = context(state, groupExpressions, subexpressionCallbacks) {
            IsNullSimulator<DummyEngine>().simulateAggregation(IsNull(expression))
        }
        val result = simulation(listOf(row))

        result shouldBe false
    }
}
