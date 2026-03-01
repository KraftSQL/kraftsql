package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Or
import rocks.frieler.kraftsql.objects.DataRow

class OrSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(value = [
        "true, true, true",
        "true, false, true",
        "true, NULL, true",
        "false, true, true",
        "false, false, false",
        "false, NULL, false",
        "NULL, true, true",
        "NULL, false, false",
        "NULL, null, false",
    ], nullValues = ["NULL"])
    fun `OrSimulator can simulate OR-operator to combine two expressions`(
        left: Boolean?,
        right: Boolean?,
        expectedResult: Boolean?
    ) {
        val row = mock<DataRow>()
        val leftHandSide =
            mock<Expression<DummyEngine, Boolean?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> left } }
        val rightHandSide =
            mock<Expression<DummyEngine, Boolean?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> right } }

        val simulation = context(subexpressionCallbacks) {
            OrSimulator<DummyEngine>().simulateExpression(Or(leftHandSide, rightHandSide))
        }
        val result = simulation(row)

        result shouldBe expectedResult
    }

    @Test
    fun `OrSimulator can simulate Or wrapping two aggregations`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, Boolean>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> true }
        }
        val rightHandSide = mock<Expression<DummyEngine, Boolean>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> true }
        }

        val simulation = context(groupExpressions, subexpressionCallbacks) {
            OrSimulator<DummyEngine>().simulateAggregation(Or(leftHandSide, rightHandSide))
        }
        val result = simulation(listOf(row))

        result shouldBe true
    }
}
