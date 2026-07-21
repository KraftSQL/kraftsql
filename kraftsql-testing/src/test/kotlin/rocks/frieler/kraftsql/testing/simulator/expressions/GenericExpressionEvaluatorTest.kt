package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Aggregation
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState

class GenericExpressionEvaluatorTest {
    private val expressionEvaluator = GenericExpressionEvaluator<DummyEngine>()

    private val state = mock<EngineState<DummyEngine>>()

    @Test
    fun `GenericExpressionEvaluator can simulate a GROUP BY Expression as an aggregation`() {
        val expression = mock<Expression<DummyEngine, Any?>>()
        val expressionSimulator = mock<ExpressionSimulator<DummyEngine, Any?, Expression<DummyEngine, Any?>>> {
            whenever(it.expression).thenReturn(expression::class)
        }.also { expressionEvaluator.registerExpressionSimulator(it) }
        val firstRow = mock<DataRow>()
        val anotherRow = mock<DataRow>()
        whenever(context(eq(state), any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            expressionSimulator.simulateExpression(eq(expression))
        })
            .thenReturn({ row : DataRow -> if (row == firstRow) 42 else 0})

        val simulation = context(state) { expressionEvaluator.simulateAggregation(expression, listOf(expression)) }
        val result = simulation.invoke(listOf(firstRow, anotherRow))

        result shouldBe 42
    }

    @Test
    fun `GenericExpressionEvaluator can simulate an Expression around a GROUP BY Expression as an aggregation`() {
        val groupByExpression = mock<Expression<DummyEngine, Any?>>()
        val expressionSimulator = mock<ExpressionSimulator<DummyEngine, Any?, Expression<DummyEngine, Any?>>> {
            whenever(it.expression).thenReturn(groupByExpression::class)
        }.also { expressionEvaluator.registerExpressionSimulator(it) }
        val expression = mock<Expression<DummyEngine, Any?>>()
        val firstRow = mock<DataRow>()
        val anotherRow = mock<DataRow>()
        whenever(context(eq(state), any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            expressionSimulator.simulateExpression(eq(groupByExpression))
        })
            .thenReturn { row -> if (row == firstRow) 42 else 0 }
        whenever(context(eq(state), any<List<Expression<DummyEngine, *>>>(), any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            expressionSimulator.simulateAggregation(eq(expression))
        })
            .thenAnswer { invocationOnMock -> { rows : List<DataRow> ->
                context(invocationOnMock.getArgument<List<Expression<DummyEngine, *>>>(1)) {
                    (invocationOnMock.getArgument<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>(2)).simulateAggregation(groupByExpression)
                }.invoke(rows) as Int * 2
            }}

        val simulation = context(state) { expressionEvaluator.simulateAggregation(expression, listOf(groupByExpression)) }
        val result = simulation.invoke(listOf(firstRow, anotherRow))

        result shouldBe 84
    }

    @Test
    fun `GenericExpressionEvaluator can simulate an Expression around an Aggregation`() {
        val aggregation = mock<Aggregation<DummyEngine, Any?>>()
        val aggregationSimulator = mock<ExpressionSimulator<DummyEngine, Any?, Aggregation<DummyEngine, Any?>>> {
            whenever(it.expression).thenReturn(aggregation::class)
        }.also { expressionEvaluator.registerExpressionSimulator(it) }
        val expression = mock<Expression<DummyEngine, Any?>>()
        val expressionSimulator = mock<ExpressionSimulator<DummyEngine, Any?, Expression<DummyEngine, Any?>>> {
            whenever(it.expression).thenReturn(expression::class)
        }.also { expressionEvaluator.registerExpressionSimulator(it) }
        val row = mock<DataRow>()
        whenever(context(eq(state), any<List<Expression<DummyEngine, *>>>(), any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            aggregationSimulator.simulateAggregation(eq(aggregation))
        })
            .thenReturn { rows -> rows.size }
        whenever(context(eq(state), any<List<Expression<DummyEngine, *>>>(), any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            expressionSimulator.simulateAggregation(eq(expression))
        })
            .thenAnswer { invocationOnMock -> { rows : List<DataRow> ->
                context(invocationOnMock.getArgument<List<Expression<DummyEngine, *>>>(1)) {
                    (invocationOnMock.getArgument<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>(2)).simulateAggregation(aggregation)
                }.invoke(rows) as Int * 2
            }}

        val simulation = context(state) { expressionEvaluator.simulateAggregation(expression, emptyList()) }
        val result = simulation.invoke(listOf(row))

        result shouldBe 2
    }

    @Test
    fun `GenericExpressionEvaluator rejects to simulate an unknown Expression`() {
        val unknownExpression = mock<Expression<DummyEngine, Nothing>>()

        shouldThrow<NotImplementedError> {
            context(state) { expressionEvaluator.simulateExpression(unknownExpression) }
        }
    }
}
