package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.ArrayElementReference
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.testing.simulator.engine.DummyEngine

class ArrayElementReferenceSimulatorTest {
    private val arrayElementReferenceSimulator = ArrayElementReferenceSimulator<DummyEngine, Any>()

    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `ArrayElementReferenceSimulator can simulate ArrayElementReference`() {
        val arrayExpression = mock<Expression<DummyEngine, Array<Any>>>()
        whenever(subexpressionCallbacks.simulateExpression(arrayExpression)).thenReturn { _ -> arrayOf("a", "b", "c") }
        val indexExpression = mock<Expression<DummyEngine, Int>>()
        whenever(subexpressionCallbacks.simulateExpression(indexExpression)).thenReturn { _ -> 2 }

        val simulatedArrayElementReference = context(subexpressionCallbacks) {
            arrayElementReferenceSimulator.simulateExpression(ArrayElementReference(arrayExpression, indexExpression))
        }
        val result = simulatedArrayElementReference(mock())

        result shouldBe "b"
    }

    @Test
    fun `ArrayElementReference on NULL array is NULL`() {
        val arrayExpression = mock<Expression<DummyEngine, Array<Any>?>>()
        whenever(subexpressionCallbacks.simulateExpression(arrayExpression)).thenReturn { _ -> null }
        val indexExpression = mock<Expression<DummyEngine, Int>>()
        whenever(subexpressionCallbacks.simulateExpression(indexExpression)).thenReturn { _ -> 1 }

        val simulatedArrayElementReference = context(subexpressionCallbacks) {
            arrayElementReferenceSimulator.simulateExpression(ArrayElementReference(arrayExpression, indexExpression))
        }
        val result = simulatedArrayElementReference(mock())

        result shouldBe null
    }

    @Test
    fun `ArrayElementReferenceSimulator can simulate ArrayElementReference as part of an aggregation`() {
        val arrayExpression = mock<Expression<DummyEngine, Array<Any>>>()
        whenever(context(any<List<Expression<DummyEngine, *>>>()) { subexpressionCallbacks.simulateAggregation(eq(arrayExpression)) })
            .thenReturn { _ -> arrayOf("a", "b", "c") }
        val indexExpression = mock<Expression<DummyEngine, Int>>()
        whenever(context(any<List<Expression<DummyEngine, *>>>()) { subexpressionCallbacks.simulateAggregation(eq(indexExpression)) })
            .thenReturn { _ -> 2 }

        val simulatedArrayElementReference = context(subexpressionCallbacks, emptyList<Expression<DummyEngine, *>>()) {
            arrayElementReferenceSimulator.simulateAggregation(ArrayElementReference(arrayExpression, indexExpression))
        }
        val result = simulatedArrayElementReference(listOf(mock()))

        result shouldBe "b"
    }
}
