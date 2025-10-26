package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNotNull
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.context

class IsNotNullSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(
        "'foo', true",
        "NULL, false",
        nullValues = ["NULL"]
    )
    fun `IsNotNullSimulator can simulate IS NOT NULL operator`(expression: String?, isNotNull: Boolean) {
        val row = mock<DataRow>()
        val expression =
            mock<Expression<DummyEngine, String?>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> expression } }

        val simulation = context(subexpressionCallbacks) {
            IsNotNullSimulator<DummyEngine>().simulateExpression(IsNotNull(expression))
        }
        val result = simulation(row)

        result shouldBe isNotNull
    }


    @Test
    fun `IsNotNullSimulator can simulate IS NOT NULL wrapping an aggregation`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mock<DataRow>()
        val expression = mock<Expression<DummyEngine, String?>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> "foo"}
        }

        val simulation = context(groupExpressions, subexpressionCallbacks) {
            IsNotNullSimulator<DummyEngine>().simulateAggregation(IsNotNull(expression))
        }
        val result = simulation(listOf(row))

        result shouldBe true
    }
}
