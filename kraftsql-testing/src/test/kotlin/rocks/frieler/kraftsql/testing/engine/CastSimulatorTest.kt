package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import java.time.LocalDate
import kotlin.reflect.typeOf

class CastSimulatorTest {
    private val subexpressionCallbacks = mock<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()

    @ParameterizedTest
    @CsvSource(
        "'true', true",
        "'false', false",
    )
    fun `CastSimulator can cast valid String to Boolean`(string: String, boolean: Boolean) {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> string }
        val booleanType = mock<Type<DummyEngine, Boolean>> { whenever(it.naturalKType()).thenReturn(typeOf<Boolean>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, Boolean>().simulateExpression(Cast(expressionToCast, booleanType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe boolean
    }

    @Test
    fun `CastSimulator can cast NULL to Boolean`() {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> null }
        val booleanType = mock<Type<DummyEngine, Boolean>> { whenever(it.naturalKType()).thenReturn(typeOf<Boolean>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, Boolean?>().simulateExpression(Cast(expressionToCast, booleanType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe null
    }

    @Test
    fun `CastSimulator can cast valid String to Int`() {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> "42" }
        val intType = mock<Type<DummyEngine, Int>> { whenever(it.naturalKType()).thenReturn(typeOf<Int>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, Int>().simulateExpression(Cast(expressionToCast, intType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe 42
    }

    @Test
    fun `CastSimulator can cast NULL to Int`() {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> null }
        val intType = mock<Type<DummyEngine, Int>> { whenever(it.naturalKType()).thenReturn(typeOf<Int>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, Int?>().simulateExpression(Cast(expressionToCast, intType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe null
    }

    @Test
    fun `CastSimulator can cast valid String to Long`() {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> "42" }
        val longType = mock<Type<DummyEngine, Long>> { whenever(it.naturalKType()).thenReturn(typeOf<Long>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, Long>().simulateExpression(Cast(expressionToCast, longType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe 42L
    }

    @Test
    fun `CastSimulator can cast NULL to Long`() {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> null }
        val longType = mock<Type<DummyEngine, Long>> { whenever(it.naturalKType()).thenReturn(typeOf<Long>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, Long?>().simulateExpression(Cast(expressionToCast, longType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe null
    }

    @Test
    fun `CastSimulator can cast Boolean to String`() {
        val expressionToCast = mock<Expression<DummyEngine, Boolean?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> true }
        val stringType = mock<Type<DummyEngine, String>> { whenever(it.naturalKType()).thenReturn(typeOf<String>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, String>().simulateExpression(Cast(expressionToCast, stringType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe "true"
    }

    @Test
    fun `CastSimulator can cast Number to String`() {
        val expressionToCast = mock<Expression<DummyEngine, Number?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> 123 }
        val stringType = mock<Type<DummyEngine, String>> { whenever(it.naturalKType()).thenReturn(typeOf<String>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, String>().simulateExpression(Cast(expressionToCast, stringType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe "123"
    }

    @Test
    fun `CastSimulator can cast String to String`() {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> "foo" }
        val stringType = mock<Type<DummyEngine, String>> { whenever(it.naturalKType()).thenReturn(typeOf<String>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, String>().simulateExpression(Cast(expressionToCast, stringType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe "foo"
    }

    @Test
    fun `CastSimulator can cast NULL to String`() {
        val expressionToCast = mock<Expression<DummyEngine, Any?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> null }
        val stringType = mock<Type<DummyEngine, String>> { whenever(it.naturalKType()).thenReturn(typeOf<String>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, String>().simulateExpression(Cast(expressionToCast, stringType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe null
    }

    @Test
    fun `CastSimulator can parse LocalDate to String`() {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> "2022-01-29" }
        val dateType = mock<Type<DummyEngine, LocalDate>> { whenever(it.naturalKType()).thenReturn(typeOf<LocalDate>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, LocalDate>().simulateExpression(Cast(expressionToCast, dateType))
        }
        val result = simulation.invoke(mock<DataRow>())

        result shouldBe LocalDate.of(2022, 1, 29)
    }

    @Test
    fun `CastSimulator trys to cast Kotlin types as fallback`() {
        val expressionToCast = mock<Expression<DummyEngine, Any?>>()
        whenever(subexpressionCallbacks.simulateExpression(expressionToCast)).thenReturn { _ -> object {} }
        val voidType = mock<Type<DummyEngine, Void>> { whenever(it.naturalKType()).thenReturn(typeOf<Void>()) }

        val simulation = context(subexpressionCallbacks) {
            CastSimulator<DummyEngine, Void>().simulateExpression(Cast(expressionToCast, voidType))
        }
        shouldThrow<ClassCastException> {
            simulation.invoke(mock<DataRow>())
        }
    }

    @Test
    fun `CastSimulator can simulate Cast wrapping an Aggregation`() {
        val expressionToCast = mock<Expression<DummyEngine, String?>>()
        whenever(context(emptyList<Expression<DummyEngine, *>>()) { subexpressionCallbacks.simulateAggregation(expressionToCast) })
            .thenReturn { _ -> "42" }
        val intType = mock<Type<DummyEngine, Int>> { whenever(it.naturalKType()).thenReturn(typeOf<Int>()) }

        val simulation = context(emptyList<Expression<DummyEngine, *>>(), subexpressionCallbacks) {
            CastSimulator<DummyEngine, Int>().simulateAggregation(Cast(expressionToCast, intType))
        }
        val result = simulation.invoke(listOf(mock<DataRow>()))

        result shouldBe 42
    }
}
