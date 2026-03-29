package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.expressions.Aggregation
import rocks.frieler.kraftsql.expressions.And
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Not
import rocks.frieler.kraftsql.expressions.Or
import rocks.frieler.kraftsql.expressions.ArrayElementReference
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNotNull
import rocks.frieler.kraftsql.expressions.LessOrEqual
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine
import kotlin.reflect.typeOf

class GenericExpressionEvaluatorTest {
    private val expressionEvaluator = GenericExpressionEvaluator<DummyEngine>()

    @Test
    fun `GenericExpressionEvaluator can simulate a GROUP BY Expression as an aggregation`() {
        val expression = mock<Expression<DummyEngine, Any?>>()
        val expressionSimulator = mock<ExpressionSimulator<DummyEngine, Any?, Expression<DummyEngine, Any?>>> {
            whenever(it.expression).thenReturn(expression::class)
        }.also { expressionEvaluator.registerExpressionSimulator(it) }
        val firstRow = mock<DataRow>()
        val anotherRow = mock<DataRow>()
        whenever(context(any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            expressionSimulator.simulateExpression(eq(expression))
        })
            .thenReturn({ row : DataRow -> if (row == firstRow) 42 else 0})

        val simulation = expressionEvaluator.simulateAggregation(expression, listOf(expression))
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
        whenever(context(any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            expressionSimulator.simulateExpression(eq(groupByExpression))
        })
            .thenReturn { row -> if (row == firstRow) 42 else 0 }
        whenever(context(any<List<Expression<DummyEngine, *>>>(), any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            expressionSimulator.simulateAggregation(eq(expression))
        })
            .thenAnswer { invocationOnMock -> { rows : List<DataRow> ->
                context(invocationOnMock.getArgument<List<Expression<DummyEngine, *>>>(0)) {
                    (invocationOnMock.getArgument<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>(1)).simulateAggregation(groupByExpression)
                }.invoke(rows) as Int * 2
            }}

        val simulation = expressionEvaluator.simulateAggregation(expression, listOf(groupByExpression))
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
        whenever(context(any<List<Expression<DummyEngine, *>>>(), any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            aggregationSimulator.simulateAggregation(eq(aggregation))
        })
            .thenReturn { rows -> rows.size }
        whenever(context(any<List<Expression<DummyEngine, *>>>(), any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) {
            expressionSimulator.simulateAggregation(eq(expression))
        })
            .thenAnswer { invocationOnMock -> { rows : List<DataRow> ->
                context(invocationOnMock.getArgument<List<Expression<DummyEngine, *>>>(0)) {
                    (invocationOnMock.getArgument<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>(1)).simulateAggregation(aggregation)
                }.invoke(rows) as Int * 2
            }}

        val simulation = expressionEvaluator.simulateAggregation(expression, emptyList())
        val result = simulation.invoke(listOf(row))

        result shouldBe 2
    }

    @Test
    fun `GenericExpressionEvaluator rejects to simulate an unknown Expression`() {
        val unknownExpression = mock<Expression<DummyEngine, Nothing>>()

        shouldThrow<NotImplementedError> {
            expressionEvaluator.simulateExpression(unknownExpression)
        }
    }

    /* ***********************************************************
     * From here on, tests for common, pre-registered Expressions:
     * ***********************************************************/

    @Test
    fun `GenericExpressionEvaluator can simulate a Constant expression`() {
        val constantExpression = Constant<DummyEngine, String>("foo")

        val simulation = expressionEvaluator.simulateExpression(constantExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe "foo"
    }

    @Test
    fun `GenericExpressionEvaluator can simulate a Column expression`() {
        val columnExpression = Column<DummyEngine, String>("foo")

        val simulation = expressionEvaluator.simulateExpression(columnExpression)
        val result = simulation.invoke(DataRow("foo" to "bar"))

        result shouldBe "bar"
    }

    @Test
    fun `GenericExpressionEvaluator can simulate a Cast`() {
        val intType = mock<Type<DummyEngine, Int>> { whenever(it.naturalKType()).thenReturn(typeOf<Int>()) }
        val castExpression = Cast(Constant("123"), intType)

        val simulation = expressionEvaluator.simulateExpression(castExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe 123
    }

    @Test
    fun `GenericExpressionEvaluator can simulate the IS NOT NULL operator`() {
        val isNotNullExpression = IsNotNull<DummyEngine>(Constant(1))

        val simulation = expressionEvaluator.simulateExpression(isNotNullExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe true
    }

    @Test
    fun `GenericExpressionEvaluator can simulate the equals-operator`() {
        val equalsExpression = Equals<DummyEngine>(Constant(1), Constant(1))

        val simulation = expressionEvaluator.simulateExpression(equalsExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe true
    }

    @Test
    fun `GenericExpressionEvaluator can simulate the LessOrEqual-operator`() {
        val lessOrEqualExpression = LessOrEqual<DummyEngine>(Constant(1), Constant(2))

        val simulation = expressionEvaluator.simulateExpression(lessOrEqualExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe true
    }

    @Test
    fun `GenericExpressionEvaluator can simulate the AND-operator`() {
        val andExpression = And<DummyEngine>(Constant(true), Constant(false))

        val simulation = expressionEvaluator.simulateExpression(andExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe false
    }

    @Test
    fun `GenericExpressionEvaluator can simulate the OR-operator`() {
        val orExpression = Or<DummyEngine>(Constant(false), Constant(true))

        val simulation = expressionEvaluator.simulateExpression(orExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe true
    }

    @Test
    fun `GenericExpressionEvaluator can simulate the NOT-operator`() {
        val notExpression = Not<DummyEngine>(Constant(true))

        val simulation = expressionEvaluator.simulateExpression(notExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe false
    }

    @Test
    fun `GenericExpressionEvaluator can simulate the COALESCE function`() {
        val coalesceExpression = Coalesce<DummyEngine, Long?>(Constant(null), Constant(42L))

        val simulation = expressionEvaluator.simulateExpression(coalesceExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe 42L
    }

    @Test
    fun `GenericExpressionEvaluator can simulate an Array expression`() {
        val arrayExpression = Array<DummyEngine, Int>(Constant(1), Constant(2))

        val simulation = expressionEvaluator.simulateExpression(arrayExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe arrayOf(1, 2)
    }

    @Test
    fun `GenericExpressionEvaluator can simulate an ArrayElementReference`() {
        val arrayElementReferenceExpression = ArrayElementReference<DummyEngine, Int>(Array(Constant(42)), Constant(1))

        val simulation = expressionEvaluator.simulateExpression(arrayElementReferenceExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe 42
    }

    @Test
    fun `GenericExpressionEvaluator can simulate an ArrayLength expression`() {
        val arrayLengthExpression = ArrayLength<DummyEngine>(Array(Constant(1), Constant(2)))

        val simulation = expressionEvaluator.simulateExpression(arrayLengthExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe 2
    }

    @Test
    fun `GenericExpressionEvaluator can simulate a Row expression`() {
        val rowExpression = Row<DummyEngine, DataRow>(mapOf("key" to Constant(1), "value" to Constant("foo")))

        val simulation = expressionEvaluator.simulateExpression(rowExpression)
        val result = simulation.invoke(DataRow())

        result shouldBe DataRow("key" to 1, "value" to "foo")
    }

    @Test
    fun `GenericExpressionEvaluator can simulate a Count aggregation`() {
        val countExpression = Count<DummyEngine>()

        val simulation = expressionEvaluator.simulateAggregation(countExpression, listOf(Constant(1)))
        val result = simulation.invoke(listOf(DataRow()))

        result shouldBe 1
    }

    @Test
    fun `GenericExpressionEvaluator can simulate a Max aggregation`() {
        val maxExpression = Max(Column<DummyEngine, Long>("value"))

        val simulation = expressionEvaluator.simulateAggregation(maxExpression, listOf(Constant(1)))
        val result = simulation.invoke(listOf(DataRow("value" to 42L)))

        result shouldBe 42L
    }
}
