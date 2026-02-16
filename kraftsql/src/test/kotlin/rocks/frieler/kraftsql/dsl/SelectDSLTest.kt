package rocks.frieler.kraftsql.dsl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.dql.CrossJoin
import rocks.frieler.kraftsql.dql.DataExpressionData
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.LeftJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.RightJoin
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
    fun `leftJoin adds LEFT JOIN of a QuerySource to the SELECT statement`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val dataToJoin = mock<QuerySource<TestableDummyEngine, *>>()
        val joinCondition = mock<Expression<TestableDummyEngine, Boolean>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            val joinedData = leftJoin(dataToJoin) { joinCondition }

            joinedData shouldBe dataToJoin
        }

        select.joins.single().shouldBeInstanceOf<LeftJoin<TestableDummyEngine>> { join ->
            join.data shouldBe dataToJoin
            join.condition shouldBe joinCondition
        }
    }

    @Test
    fun `leftJoin adds LEFT JOIN of Data to the SELECT statement`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val dataToJoin = mock<Data<TestableDummyEngine, *>>()
        val joinCondition = mock<Expression<TestableDummyEngine, Boolean>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            val joinedData = leftJoin(dataToJoin) { joinCondition }

            joinedData.shouldBeInstanceOf<QuerySource<TestableDummyEngine, *>> { it.data shouldBe dataToJoin }
        }

        select.joins.single().shouldBeInstanceOf<LeftJoin<TestableDummyEngine>> { join ->
            join.data.shouldBeInstanceOf<QuerySource<TestableDummyEngine, *>> { it.data shouldBe dataToJoin }
            join.condition shouldBe joinCondition
        }
    }

    @Test
    fun `rightJoin adds RIGHT JOIN of a QuerySource to the SELECT statement`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val dataToJoin = mock<QuerySource<TestableDummyEngine, *>>()
        val joinCondition = mock<Expression<TestableDummyEngine, Boolean>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            val joinedData = rightJoin(dataToJoin) { joinCondition }

            joinedData shouldBe dataToJoin
        }

        select.joins.single().shouldBeInstanceOf<RightJoin<TestableDummyEngine>> { join ->
            join.data shouldBe dataToJoin
            join.condition shouldBe joinCondition
        }
    }

    @Test
    fun `rightJoin adds RIGHT JOIN of Data to the SELECT statement`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val dataToJoin = mock<Data<TestableDummyEngine, *>>()
        val joinCondition = mock<Expression<TestableDummyEngine, Boolean>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            val joinedData = rightJoin(dataToJoin) { joinCondition }

            joinedData.shouldBeInstanceOf<QuerySource<TestableDummyEngine, *>> { it.data shouldBe dataToJoin }
        }

        select.joins.single().shouldBeInstanceOf<RightJoin<TestableDummyEngine>> { join ->
            join.data.shouldBeInstanceOf<QuerySource<TestableDummyEngine, *>> { it.data shouldBe dataToJoin }
            join.condition shouldBe joinCondition
        }
    }

    @Test
    fun `crossJoin adds CROSS JOIN of a QuerySource to the SELECT statement`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val dataToJoin = mock<QuerySource<TestableDummyEngine, *>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            val joinedData = crossJoin(dataToJoin)

            joinedData shouldBe dataToJoin
        }

        select.joins.single().shouldBeInstanceOf<CrossJoin<TestableDummyEngine>> { join ->
            join.data shouldBe dataToJoin
        }
    }

    @Test
    fun `crossJoin adds CROSS JOIN of Data to the SELECT statement`() {
        val source = mock<Data<TestableDummyEngine, *>>()
        val dataToJoin = mock<Data<TestableDummyEngine, *>>()

        val select = Select<TestableDummyEngine, DataRow> {
            from(source)
            val joinedData = crossJoin(dataToJoin)

            joinedData.shouldBeInstanceOf<QuerySource<TestableDummyEngine, *>> { it.data shouldBe dataToJoin }
        }

        select.joins.single().shouldBeInstanceOf<CrossJoin<TestableDummyEngine>> { join ->
            join.data.shouldBeInstanceOf<QuerySource<TestableDummyEngine, *>> { it.data shouldBe dataToJoin }
        }
    }

    @Test
    fun `as() wraps Data into a QuerySource with alias`() {
        val data = mock<Data<TestableDummyEngine, *>>()

        val querySource = data `as` "alias"

        querySource.data shouldBe data
        querySource.alias shouldBe "alias"
    }

    @Test
    fun `as() wraps Data-valued Expression into a QuerySource with alias`() {
        val expression = mock<Expression<TestableDummyEngine, Data<TestableDummyEngine, DataRow>>>()

        val querySource : QuerySource<TestableDummyEngine, DataRow> = expression `as` "alias"

        querySource.data shouldBe DataExpressionData(expression)
        querySource.alias shouldBe "alias"
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
