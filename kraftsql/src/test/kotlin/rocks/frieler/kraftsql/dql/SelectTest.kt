package rocks.frieler.kraftsql.dql

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow

class SelectTest {
    private val source = mock<QuerySource<TestableDummyEngine, DataRow>>()

    @Test
    fun `columnNames provides the explicitly selected columns`() {
        val projectionWithAlias = mock<Projection<TestableDummyEngine, *>> { whenever(it.alias).thenReturn("a") }
        val projectionWithoutAlias = mock<Projection<TestableDummyEngine, *>> {
            whenever(it.alias).thenReturn(null)
            val value = mock<Expression<TestableDummyEngine, *>> { e -> whenever(e.defaultColumnName()).thenReturn("x") }
            whenever(it.value).thenReturn(value)
        }

        val select = Select<TestableDummyEngine, DataRow>(source, columns = listOf(projectionWithAlias, projectionWithoutAlias))

        select.columnNames shouldBe listOf(projectionWithAlias.alias, projectionWithoutAlias.value.defaultColumnName())
    }

    @Test
    fun `columnNames provides columns from underlying source selected by wildcard`() {
        whenever(source.columnNames).thenReturn(listOf("c1", "c2"))

        val select = Select<TestableDummyEngine, DataRow>(source)

        select.columnNames shouldBe source.columnNames
    }

    @Test
    fun `columnNames provides columns from underlying source and joined sources selected by wildcard`() {
        whenever(source.columnNames).thenReturn(listOf("c1", "c2"))
        val joinedData = mock<QuerySource<TestableDummyEngine, DataRow>> { whenever(it.columnNames).thenReturn(listOf("c3")) }

        val select = Select<TestableDummyEngine, DataRow>(source, joins = listOf(InnerJoin(joinedData, mock())))

        select.columnNames shouldBe source.columnNames + joinedData.columnNames
    }

    @Test
    fun `columnNames provides grouping expressions when group by is present and columns are selected by wildcard`() {
        val groupingExpression1 = mock<Expression<TestableDummyEngine, *>> { whenever(it.defaultColumnName()).thenReturn("g1") }
        val groupingExpression2 = mock<Expression<TestableDummyEngine, *>> { whenever(it.defaultColumnName()).thenReturn("g2") }

        val select = Select<TestableDummyEngine, DataRow>(source, grouping = listOf(groupingExpression1, groupingExpression2))

        select.columnNames shouldBe listOf(groupingExpression1.defaultColumnName(), groupingExpression2.defaultColumnName())
    }
}
