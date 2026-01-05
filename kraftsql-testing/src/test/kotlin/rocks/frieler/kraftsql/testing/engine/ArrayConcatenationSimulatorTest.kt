package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

class ArrayConcatenationSimulatorTest {
    @Suppress("UNCHECKED_CAST")
    private val arrayConcatenationSimulator = ArrayConcatenationSimulator(ArrayConcatenation::class as KClass<out ArrayConcatenation<DummyEngine, Int>>)

    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `ArrayConcatenationSimulator can simulate ArrayConcatenation of multiple Arrays`() {
        val arrayExpression1 = mock<Expression<DummyEngine, Array<Int>?>>()
        whenever(subexpressionCallbacks.simulateExpression(arrayExpression1)).thenReturn { _ -> arrayOf(1, 2) }
        val arrayExpression2 = mock<Expression<DummyEngine, Array<Int>?>>()
        whenever(subexpressionCallbacks.simulateExpression(arrayExpression2)).thenReturn { _ -> arrayOf(3, 4) }
        val arrayConcatenation = mock<ArrayConcatenation<DummyEngine, Int>> {
            whenever(it.arguments).thenReturn(arrayOf(arrayExpression1, arrayExpression2))
        }

        val simulation = context(subexpressionCallbacks) {
            arrayConcatenationSimulator.simulateExpression(arrayConcatenation)
        }
        val result = simulation(mock<DataRow>())

        result shouldBe arrayOf(1, 2, 3, 4)
    }

    @Test
    fun `ArrayConcatenationSimulator returns NULL if any of the Arrays to concatenate is NULL`() {
        val arrayExpression1 = mock<Expression<DummyEngine, Array<Int>?>>()
        whenever(subexpressionCallbacks.simulateExpression(arrayExpression1)).thenReturn { _ -> arrayOf(1, 2) }
        val arrayExpression2 = mock<Expression<DummyEngine, Array<Int>?>>()
        whenever(subexpressionCallbacks.simulateExpression(arrayExpression2)).thenReturn { _ -> null }
        val arrayConcatenation = mock<ArrayConcatenation<DummyEngine, Int>> {
            whenever(it.arguments).thenReturn(arrayOf(arrayExpression1, arrayExpression2))
        }

        val simulation = context(subexpressionCallbacks) {
            arrayConcatenationSimulator.simulateExpression(arrayConcatenation)
        }
        val result = simulation(mock<DataRow>())

        result shouldBe null
    }

    @Test
    fun `ArrayConcatenationSimulator can simulate ArrayConcatenation of multiple Arrays as part of an aggregation`() {
        val arrayExpression1 = mock<Expression<DummyEngine, Array<Int>?>>()
        whenever(context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(arrayExpression1) })
            .thenReturn { _ -> arrayOf(1, 2) }
        val arrayExpression2 = mock<Expression<DummyEngine, Array<Int>?>>()
        whenever(context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(arrayExpression2) })
            .thenReturn { _ -> arrayOf(3, 4) }
        val arrayConcatenation = mock<ArrayConcatenation<DummyEngine, Int>> {
            whenever(it.arguments).thenReturn(arrayOf(arrayExpression1, arrayExpression2))
        }

        val simulation = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            arrayConcatenationSimulator.simulateAggregation(arrayConcatenation)
        }
        val result = simulation(listOf(mock<DataRow>()))

        result shouldBe arrayOf(1, 2, 3, 4)
    }
}
