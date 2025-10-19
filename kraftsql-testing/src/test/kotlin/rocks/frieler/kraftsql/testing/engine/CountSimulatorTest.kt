package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Expression
import java.sql.SQLException

class CountSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `CountSimulator rejects simulation as Expression`() {
        shouldThrow<SQLException> {
            context(subexpressionCallbacks) {
                CountSimulator<DummyEngine>().simulateExpression(mock())
            }
        }
    }

    @Test
    fun `CountSimulator can simulate Count as aggregation over multiple rows`() {
        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            CountSimulator<DummyEngine>().simulateAggregation(Count())
        }
        val result = simulation(listOf(mock(), mock()))

        result shouldBe 2L
    }
}
