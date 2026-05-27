package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.SubqueryExpression
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import rocks.frieler.kraftsql.testing.simulator.engine.GenericQueryEvaluator
import java.sql.SQLException

class SubqueryExpressionSimulatorTest {
    private val queryEvaluator = mockk<GenericQueryEvaluator<DummyEngine>>()
    private val subqueryExpressionSimulator = SubqueryExpressionSimulator<DummyEngine, Any?>(queryEvaluator)

    private val state = mockk<EngineState<DummyEngine>>()
    private val subexpressionCallbacks = mockk<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `SubqueryExpressionSimulator can simulate a subquery as Expression`() {
        val subquery = mockk<Select<DummyEngine, Any>>()
        every { (context(state) { queryEvaluator.selectRows(subquery) }) }
            .answers { listOf(DataRow("value" to 42)) }
        val subqueryExpression = SubqueryExpression<DummyEngine, Any?>(subquery)

        val simulation = context(state, subexpressionCallbacks) {
            subqueryExpressionSimulator.simulateExpression(subqueryExpression)
        }

        simulation(mockk()) shouldBe 42
    }

    @Test
    fun `SubqueryExpressionSimulator simulates a subquery without rows as null`() {
        val subquery = mockk<Select<DummyEngine, Any>>()
        every { (context(state) { queryEvaluator.selectRows(subquery) }) }
            .answers { emptyList() }
        val subqueryExpression = SubqueryExpression<DummyEngine, Any?>(subquery)

        val simulation = context(state, subexpressionCallbacks) {
            subqueryExpressionSimulator.simulateExpression(subqueryExpression)
        }

        simulation(mockk()) shouldBe null
    }

    @Test
    fun `SubqueryExpressionSimulator fails when the subquery returns more than one row`() {
        val subquery = mockk<Select<DummyEngine, Any>>()
        every { (context(state) { queryEvaluator.selectRows(subquery) }) }
            .answers { listOf(DataRow("value" to 1), DataRow("value" to 2)) }
        val subqueryExpression = SubqueryExpression<DummyEngine, Any?>(subquery)

        val simulation = context(state, subexpressionCallbacks) {
            subqueryExpressionSimulator.simulateExpression(subqueryExpression)
        }

        val exception = shouldThrow<SQLException> { simulation(mockk()) }
        exception.message shouldContain "more than one row"
    }

    @Test
    fun `SubqueryExpressionSimulator fails when the subquery returns a row with not exactly one value`() {
        val subquery = mockk<Select<DummyEngine, Any>>()
        every { (context(state) { queryEvaluator.selectRows(subquery) }) }
            .answers { listOf(DataRow("first" to 1, "second" to 2)) }
        val subqueryExpression = SubqueryExpression<DummyEngine, Any?>(subquery)

        val simulation = context(state, subexpressionCallbacks) {
            subqueryExpressionSimulator.simulateExpression(subqueryExpression)
        }

        val exception = shouldThrow<SQLException> { simulation(mockk()) }
        exception.message shouldContain "not exactly one value"
    }

    @Test
    fun `SubqueryExpressionSimulator can simulate a subquery as aggregation`() {
        val subquery = mockk<Select<DummyEngine, Any>>()
        every { (context(state) { queryEvaluator.selectRows(subquery) }) }
            .answers { listOf(DataRow("value" to 42)) }
        val subqueryExpression = SubqueryExpression<DummyEngine, Any?>(subquery)

        val simulation = context(state, emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            subqueryExpressionSimulator.simulateAggregation(subqueryExpression)
        }

        simulation(listOf(mockk())) shouldBe 42
    }
}
