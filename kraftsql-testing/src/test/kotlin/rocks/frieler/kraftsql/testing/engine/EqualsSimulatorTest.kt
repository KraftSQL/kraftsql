package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow

class EqualsSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(
        "'foo', 'foo', true",
        "'foo', 'bar', false",
    )
    fun `EqualsSimulator can simulate equals-operator to compare two expressions`(left: String, right: String, equals: Boolean) {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, String>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> left} }
        val rightHandSide = mock<Expression<DummyEngine, String>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> right} }

        val simulation = context(subexpressionCallbacks) {
            EqualsSimulator<DummyEngine>().simulateExpression(Equals(leftHandSide, rightHandSide))
        }
        val result = simulation(row)

        result shouldBe equals
    }

    @Test
    fun `EqualsSimulator can simulate Equals wrapping two aggregations`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, String>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> "left"}
        }
        val rightHandSide = mock<Expression<DummyEngine, String>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> "right"}
        }

        val simulation = context(groupExpressions, subexpressionCallbacks) {
            EqualsSimulator<DummyEngine>().simulateAggregation(Equals(leftHandSide, rightHandSide))
        }
        val result = simulation(listOf(row))

        result shouldBe false
    }
}
