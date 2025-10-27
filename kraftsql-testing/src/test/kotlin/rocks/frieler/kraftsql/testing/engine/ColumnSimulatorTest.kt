package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import java.sql.SQLSyntaxErrorException

class ColumnSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `ColumnSimulator returns function that returns the Column's value from a DataRow`() {
        val row = mock<DataRow> {
            whenever(it["foo"]).thenReturn("bar")
        }

        val simulation = context(subexpressionCallbacks) {
            ColumnSimulator<DummyEngine, String>().simulateExpression(Column("foo"))
        }
        val value = simulation(row)

        value shouldBe "bar"
        verifyNoInteractions(subexpressionCallbacks)
    }

    @Test
    fun `ColumnSimulator returns function that returns a nullable Column's value from a DataRow`() {
        val row = mock<DataRow> {
            whenever(it["foo"]).thenReturn(null)
        }

        val simulation = context(subexpressionCallbacks) {
            ColumnSimulator<DummyEngine, String?>().simulateExpression(Column("foo"))
        }
        val value = simulation(row)

        value shouldBe null
        verifyNoInteractions(subexpressionCallbacks)
    }

    @Test
    fun `simulateAggregation() returns function that returns the Column's value from any DataRow if it is a GROUP BY expression`() {
        val rows = listOf(
            mock<DataRow> { whenever(it["group"]).thenReturn("foo") },
            mock<DataRow> { whenever(it["group"]).thenReturn("foo") },
        )
        val columnExpression = Column<DummyEngine, String>("group")

        val simulation = context(listOf(columnExpression), subexpressionCallbacks) {
            ColumnSimulator<DummyEngine, String>().simulateAggregation(columnExpression)
        }
        val value = simulation(rows)

        value shouldBe "foo"
        verifyNoInteractions(subexpressionCallbacks)
    }

    @Test
    fun `simulateAggregation() rejects Column expression that is not part of the GROUP BY clause`() {
        val columnExpression = Column<DummyEngine, String>("foo")

        shouldThrow<SQLSyntaxErrorException> {
            context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
                ColumnSimulator<DummyEngine, String>().simulateAggregation(columnExpression)
            }
        }

        verifyNoInteractions(subexpressionCallbacks)
    }
}
