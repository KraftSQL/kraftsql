package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow

class ConstantSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `simulateExpression() returns function that returns the Constant's value`() {
        val row = mock<DataRow>()

        val simulation = context(subexpressionCallbacks) {
            ConstantSimulator<DummyEngine, Long>().simulateExpression(Constant(42L))
        }
        val value = simulation(row)

        value shouldBe 42L
        verifyNoInteractions(subexpressionCallbacks, row)
    }

    @Test
    fun `simulateAggregation() returns function that returns the Constant's value`() {
        val rows = listOf(mock<DataRow>())

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            ConstantSimulator<DummyEngine, Long>().simulateAggregation(Constant(42L))
        }
        val value = simulation(rows)

        value shouldBe 42L
        verifyNoInteractions(subexpressionCallbacks, mock<DataRow>())
    }
}
