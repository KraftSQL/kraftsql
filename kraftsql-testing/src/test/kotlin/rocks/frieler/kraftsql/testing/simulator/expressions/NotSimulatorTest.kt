package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Not
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine

class NotSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(value = [
        "true, false",
        "false, true",
        "NULL, false",
    ], nullValues = ["NULL"])
    fun `NotSimulator can simulate NOT-operator to negate an expression`(value: Boolean?, expectedResult: Boolean?) {
        val row = mock<DataRow>()
        val innerExpression = mock<Expression<DummyEngine, Boolean?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> value} }

        val simulation = context(subexpressionCallbacks) {
            NotSimulator<DummyEngine>().simulateExpression(Not(innerExpression))
        }
        val result = simulation(row)

        result shouldBe expectedResult
    }

    @Test
    fun `NotSimulator can simulate Not wrapping an aggregation`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mock<DataRow>()
        val innerExpression = mock<Expression<DummyEngine, Boolean>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> false}
        }

        val simulation = context(groupExpressions, subexpressionCallbacks) {
            NotSimulator<DummyEngine>().simulateAggregation(Not(innerExpression))
        }
        val result = simulation(listOf(row))

        result shouldBe true
    }
}
