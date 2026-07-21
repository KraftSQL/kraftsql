package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.expressions.And
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.ArrayElementReference
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.IsNotNull
import rocks.frieler.kraftsql.expressions.LessOrEqual
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.expressions.Min
import rocks.frieler.kraftsql.expressions.Not
import rocks.frieler.kraftsql.expressions.Or
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.expressions.SubqueryExpression
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericExpressionEvaluator
import kotlin.reflect.typeOf

class GenericEngineSimulatorBuilderTemplateTest {
    private val builder = object : GenericEngineSimulatorBuilderTemplate<DummyEngine, GenericEngineSimulator<DummyEngine>>() {
        val expressionEvaluatorExposed = expressionEvaluator
        override fun makeSimulator() = mockk<GenericEngineSimulator<DummyEngine>>()
    }.also { it.build() }


    @Test
    fun `GenericEngineSimulatorBuilderTemplate cannot build more than one instance`() {
        shouldThrow<IllegalStateException> { builder.build() }
    }

    @Test
    fun `GenericEngineSimulatorBuilderTemplate ensures QueryEvaluator uses the ExpressionEvaluator`() {
        val brokenBuilder = object : GenericEngineSimulatorBuilderTemplate<DummyEngine, GenericEngineSimulator<DummyEngine>>() {
            override val expressionEvaluator = mockk<GenericExpressionEvaluator<DummyEngine>>()
            override val queryEvaluator = mockk<GenericQueryEvaluator<DummyEngine>> {
                every { expressionEvaluatorForChecking } returns mockk()
            }
            override fun makeSimulator() = mockk<GenericEngineSimulator<DummyEngine>>()
        }

        shouldThrow<IllegalArgumentException> {
            brokenBuilder.build()
        }
    }

    /* ***********************************************************
     * From here on, tests for common, pre-registered Expressions:
     * ***********************************************************/
    private val expressionEvaluator = builder.expressionEvaluatorExposed
    private val state = mock<EngineState<DummyEngine>>()

    @Test
    fun `Wired ExpressionEvaluator can simulate a Constant expression`() {
        val constantExpression = Constant<DummyEngine, String>("foo")

        val simulation = context(state) { expressionEvaluator.simulateExpression(constantExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe "foo"
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate a Column expression`() {
        val columnExpression = Column<DummyEngine, String>("foo")

        val simulation = context(state) { expressionEvaluator.simulateExpression(columnExpression) }
        val result = simulation.invoke(DataRow("foo" to "bar"))

        result shouldBe "bar"
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate a Cast`() {
        val intType = mock<Type<DummyEngine, Int>> { whenever(it.naturalKType()).thenReturn(typeOf<Int>()) }
        val castExpression = Cast(Constant("123"), intType)

        val simulation = context(state) { expressionEvaluator.simulateExpression(castExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe 123
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate the IS NOT NULL operator`() {
        val isNotNullExpression = IsNotNull<DummyEngine>(Constant(1))

        val simulation = context(state) { expressionEvaluator.simulateExpression(isNotNullExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe true
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate the equals-operator`() {
        val equalsExpression = Equals<DummyEngine>(Constant(1), Constant(1))

        val simulation = context(state) { expressionEvaluator.simulateExpression(equalsExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe true
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate the LessOrEqual-operator`() {
        val lessOrEqualExpression = LessOrEqual<DummyEngine>(Constant(1), Constant(2))

        val simulation = context(state) { expressionEvaluator.simulateExpression(lessOrEqualExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe true
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate the NOT-operator`() {
        val notExpression = Not<DummyEngine>(Constant(true))

        val simulation = context(state) { expressionEvaluator.simulateExpression(notExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe false
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate the AND-operator`() {
        val andExpression = And<DummyEngine>(Constant(true), Constant(false))

        val simulation = context(state) { expressionEvaluator.simulateExpression(andExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe false
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate the OR-operator`() {
        val orExpression = Or<DummyEngine>(Constant(false), Constant(true))

        val simulation = context(state) { expressionEvaluator.simulateExpression(orExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe true
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate the COALESCE function`() {
        val coalesceExpression = Coalesce<DummyEngine, Long?>(Constant(null), Constant(42L))

        val simulation = context(state) { expressionEvaluator.simulateExpression(coalesceExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe 42L
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate an Array expression`() {
        val arrayExpression = Array<DummyEngine, Int>(Constant(1), Constant(2))

        val simulation = context(state) { expressionEvaluator.simulateExpression(arrayExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe arrayOf(1, 2)
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate an ArrayElementReference`() {
        val arrayElementReferenceExpression = ArrayElementReference<DummyEngine, Int>(Array(Constant(42)), Constant(1))

        val simulation = context(state) { expressionEvaluator.simulateExpression(arrayElementReferenceExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe 42
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate an ArrayLength expression`() {
        val arrayLengthExpression = ArrayLength<DummyEngine>(Array(Constant(1), Constant(2)))

        val simulation = context(state) { expressionEvaluator.simulateExpression(arrayLengthExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe 2
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate a Row expression`() {
        val rowExpression = Row<DummyEngine, DataRow>(mapOf("key" to Constant(1), "value" to Constant("foo")))

        val simulation = context(state) { expressionEvaluator.simulateExpression(rowExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe DataRow("key" to 1, "value" to "foo")
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate a SubqueryExpression`() {
        val subquery = Select<DummyEngine, DataRow>(source = QuerySource(ConstantData(DummyEngine.orm, DataRow("answer" to 42L))))
        val subqueryExpression = SubqueryExpression<DummyEngine, Long>(subquery)

        val simulation = context(state) { expressionEvaluator.simulateExpression(subqueryExpression) }
        val result = simulation.invoke(DataRow())

        result shouldBe 42L
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate a Count aggregation`() {
        val countExpression = Count<DummyEngine>()

        val simulation = context(state) { expressionEvaluator.simulateAggregation(countExpression, listOf(Constant(1))) }
        val result = simulation.invoke(listOf(DataRow()))

        result shouldBe 1
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate a Min aggregation`() {
        val maxExpression = Min(Column<DummyEngine, Long>("value"))

        val simulation = context(state) { expressionEvaluator.simulateAggregation(maxExpression, listOf(Constant(1))) }
        val result = simulation.invoke(listOf(DataRow("value" to 42L)))

        result shouldBe 42L
    }

    @Test
    fun `Wired ExpressionEvaluator can simulate a Max aggregation`() {
        val maxExpression = Max(Column<DummyEngine, Long>("value"))

        val simulation = context(state) { expressionEvaluator.simulateAggregation(maxExpression, listOf(Constant(1))) }
        val result = simulation.invoke(listOf(DataRow("value" to 42L)))

        result shouldBe 42L
    }
}
