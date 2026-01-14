package rocks.frieler.kraftsql.dsl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

class SelectDSLTest {
    @Test
    fun `Select builds a Select statement using the given DSL configurator`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val projection = mock<Projection<TestableDummyEngine, *>>()
        val filter = mock<Expression<TestableDummyEngine, Boolean>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            column(projection)
            where(filter)
        }

        select.source.data shouldBe source
        select.source.alias shouldBe null
        select.columns shouldBe listOf(projection)
        select.filter shouldBe filter
    }

    @Test
    fun `innerJoin adds INNER JOIN of a QuerySource to the SELECT statement`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val dataToJoin = mock<QuerySource<TestableDummyEngine, *>>()
        val joinCondition = mock<Expression<TestableDummyEngine, Boolean>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            val joinedData = innerJoin(dataToJoin) { joinCondition }

            joinedData shouldBe dataToJoin
        }

        select.joins.single().shouldBeInstanceOf<InnerJoin<TestableDummyEngine>> { join ->
            join.data shouldBe dataToJoin
            join.condition shouldBe joinCondition
        }
    }

    @Test
    fun `innerJoin adds INNER JOIN of Data to the SELECT statement`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val dataToJoin = mock<Data<TestableDummyEngine, *>>()
        val joinCondition = mock<Expression<TestableDummyEngine, Boolean>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            val joinedData = innerJoin(dataToJoin) { joinCondition }

            joinedData.shouldBeInstanceOf<QuerySource<TestableDummyEngine, *>> { it.data shouldBe  dataToJoin }
        }

        select.joins.single().shouldBeInstanceOf<InnerJoin<TestableDummyEngine>> { join ->
            join.data.shouldBeInstanceOf<QuerySource<TestableDummyEngine, *>> { it.data shouldBe  dataToJoin }
            join.condition shouldBe joinCondition
        }
    }

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
