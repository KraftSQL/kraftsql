package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow

class ArraySimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @Test
    fun `ArraySimulator can simulate NULL constant`() {
        val simulation = context(subexpressionCallbacks) {
            ArraySimulator<DummyEngine, Any>().simulateExpression(Array(null))
        }
        val result = simulation(mock<DataRow>())

        result shouldBe null
    }

    @Test
    fun `ArraySimulator can simulate Array() of multiple expressions`() {
        val simulation = context(subexpressionCallbacks) {
            ArraySimulator<DummyEngine, String>().simulateExpression(Array(
                mock<Expression<DummyEngine, String>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> "foo"} },
                mock<Expression<DummyEngine, String>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> "bar"} },
            ))
        }
        val result = simulation(mock<DataRow>())

        result shouldBe arrayOf("foo", "bar")
    }

    @Test
    fun `ArraySimulator creates Array of common supertype of the elements`() {
        val simulation = context(subexpressionCallbacks) {
            ArraySimulator<DummyEngine, Number>().simulateExpression(Array(
                mock<Expression<DummyEngine, Int>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 42} },
                mock<Expression<DummyEngine, Double>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 3.14} },
            ))
        }
        val result = simulation(mock<DataRow>())

        result!!::class.java.componentType shouldBe Number::class.java
        result shouldBe arrayOf<Number>(42, 3.14)
    }


    @Test
    fun `ArraySimulator can simulate Array() of multiple aggregations`() {
        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            ArraySimulator<DummyEngine, String>().simulateAggregation(Array(
                mock<Expression<DummyEngine, String>>().also { whenever(subexpressionCallbacks.simulateAggregation(it)).thenReturn { _ -> "foo"} },
                mock<Expression<DummyEngine, String>>().also { whenever(subexpressionCallbacks.simulateAggregation(it)).thenReturn { _ -> "bar"} },
            ))
        }
        val result = simulation(listOf(mock<DataRow>()))

        result shouldBe arrayOf("foo", "bar")
    }
}
