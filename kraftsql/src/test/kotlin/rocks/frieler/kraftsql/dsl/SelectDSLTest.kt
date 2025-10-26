package rocks.frieler.kraftsql.dsl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Expression
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

class SelectDSLTest {
    @Test
    fun `as() with KProperty returns Projection of Expression named by property`() {
        val expression = mock<Expression<TestableDummyEngine, Any>>()
        val property = mock<KProperty<Any>> {
            whenever(it.name).thenReturn("column")
            whenever(it.returnType).thenReturn(typeOf<Any>())
        }

        val projection = expression `as` property

        projection.value shouldBe expression
        projection.alias shouldBe property.name
    }

    @Test
    fun `as() with KProperty rejects nullable Expression to be assigned to non-nullable property`() {
        val nullableExpression = mock<Expression<TestableDummyEngine, Any?>>()
        val nonNullableProperty = mock<KProperty<Any>> { whenever(it.returnType).thenReturn(typeOf<Any>()) }

        shouldThrow<IllegalArgumentException> {
            nullableExpression `as` nonNullableProperty
        }
    }
}
