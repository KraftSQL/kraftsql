package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import java.math.BigDecimal
import kotlin.reflect.KClass

class EqualsSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(
        "'foo', 'foo', true",
        "'foo', 'bar', false",
    )
    fun `EqualsSimulator can simulate equals-operator to compare two expressions`(left: String, right: String, equals: Boolean) {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, String>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> left} }
        val rightHandSide = mock<Expression<DummyEngine, String>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> right} }

        val simulation = context(subexpressionCallbacks) {
            EqualsSimulator<DummyEngine>().simulateExpression(Equals(leftHandSide, rightHandSide))
        }
        val result = simulation(row)

        result shouldBe equals
    }

    @Test
    fun `EqualsSimulator returns false when comparing to NULL`() {
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, *>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> null} }
        val rightHandSide = mock<Expression<DummyEngine, *>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> null} }

        val simulation = context(subexpressionCallbacks) {
            EqualsSimulator<DummyEngine>().simulateExpression(Equals(leftHandSide, rightHandSide))
        }
        val result = simulation(row)

        result shouldBe false
    }

    @Test
    fun `EqualsSimulator returns true for equal integer numbers of different types`() {
        val numberTypes = listOf(Byte::class, Short::class, Int::class, Long::class, BigDecimal::class)
        fun Int.toNumberType(type: KClass<out Number>) =
            when (type) {
                Byte::class -> toByte()
                Short::class -> toShort()
                Int::class -> this
                Long::class -> toLong()
                BigDecimal::class -> toBigDecimal()
                else -> throw IllegalArgumentException("unknown type $type")
            }
        for (leftType in numberTypes) for (rightType in numberTypes) if (leftType != rightType) {

            val row = mock<DataRow>()
            val leftHandSide =
                mock<Expression<DummyEngine, Number>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 42.toNumberType(leftType) } }
            val rightHandSide =
                mock<Expression<DummyEngine, Number>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 42.toNumberType(rightType) } }

            val simulation = context(subexpressionCallbacks) {
                EqualsSimulator<DummyEngine>().simulateExpression(Equals(leftHandSide, rightHandSide))
            }
            val result = simulation(row)

            withClue("comparing $leftType and $rightType:") {
                result shouldBe true
            }
        }
    }

    @Test
    fun `EqualsSimulator returns true for equal floating point numbers of different types`() {
        val numberTypes = listOf(Float::class, Double::class, BigDecimal::class)
        fun Float.toNumberType(type: KClass<out Number>) =
            when (type) {
                Float::class -> toFloat()
                Double::class -> toDouble()
                BigDecimal::class -> this
                else -> throw IllegalArgumentException("unknown type $type")
            }
        for (leftType in numberTypes) for (rightType in numberTypes) if (leftType != rightType) {

            val row = mock<DataRow>()
            val leftHandSide =
                mock<Expression<DummyEngine, Number>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 42F.toNumberType(leftType) } }
            val rightHandSide =
                mock<Expression<DummyEngine, Number>>().also { whenever(subexpressionCallbacks.simulateExpression(it)).thenReturn { _ -> 42F.toNumberType(rightType) } }

            val simulation = context(subexpressionCallbacks) {
                EqualsSimulator<DummyEngine>().simulateExpression(Equals(leftHandSide, rightHandSide))
            }
            val result = simulation(row)

            withClue("comparing $leftType and $rightType:") {
                result shouldBe true
            }
        }
    }

    @Test
    fun `EqualsSimulator can simulate Equals wrapping two aggregations`() {
        val groupExpressions = emptyList<Expression<DummyEngine, *>>()
        val row = mock<DataRow>()
        val leftHandSide = mock<Expression<DummyEngine, String>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> "left"}
        }
        val rightHandSide = mock<Expression<DummyEngine, String>>().also {
            context(groupExpressions) { whenever(subexpressionCallbacks.simulateAggregation(it)) }.thenReturn { _ -> "right"}
        }

        val simulation = context(groupExpressions, subexpressionCallbacks) {
            EqualsSimulator<DummyEngine>().simulateAggregation(Equals(leftHandSide, rightHandSide))
        }
        val result = simulation(listOf(row))

        result shouldBe false
    }
}
