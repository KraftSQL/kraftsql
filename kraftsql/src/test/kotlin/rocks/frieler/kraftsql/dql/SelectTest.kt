package rocks.frieler.kraftsql.dql

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.objects.DataRow

class SelectTest {
    private val source = mock<QuerySource<TestableDummyEngine, DataRow>>()

    @Test
    fun `selectableColumnNames provides the explicitly selected and named expressions`() {
        val projectionWithAlias = mock<Projection<TestableDummyEngine, *>> { whenever(it.alias).thenReturn("a") }
        val projectionWithoutAlias = mock<Projection<TestableDummyEngine, *>> {
            whenever(it.alias).thenReturn(null)
        }

        val select = Select<TestableDummyEngine, DataRow>(source, columns = listOf(projectionWithAlias, projectionWithoutAlias))

        select.selectableColumnNames shouldBe listOf(projectionWithAlias.alias)
    }

    @Test
    fun `selectableColumnNames provides the name from selected Column without alias`() {
        val selectedColumn = mock<Column<TestableDummyEngine, *>> { whenever(it.qualifiedName).thenReturn("data.col") }
        val projectionOfColumnWithoutAlias = mock<Projection<TestableDummyEngine, *>> {
            whenever(it.alias).thenReturn(null)
            whenever(it.value).thenReturn(selectedColumn)
        }

        val select = Select<TestableDummyEngine, DataRow>(source, columns = listOf(projectionOfColumnWithoutAlias))

        select.selectableColumnNames shouldBe listOf(selectedColumn.qualifiedName)
    }

    @Test
    fun `selectableColumnNames provides selectable columns from underlying source selected by wildcard`() {
        whenever(source.selectableColumnNames).thenReturn(listOf("c1", "c2"))

        val select = Select<TestableDummyEngine, DataRow>(source)

        select.selectableColumnNames shouldBe source.selectableColumnNames
    }

    @Test
    fun `selectableColumnNames provides selectable columns from underlying source and joined sources selected by wildcard`() {
        whenever(source.selectableColumnNames).thenReturn(listOf("c1", "c2"))
        val joinedData = mock<QuerySource<TestableDummyEngine, DataRow>> { whenever(it.selectableColumnNames).thenReturn(listOf("c3")) }

        val select = Select<TestableDummyEngine, DataRow>(source, joins = listOf(InnerJoin(joinedData, mock())))

        select.selectableColumnNames shouldBe source.selectableColumnNames + joinedData.selectableColumnNames
    }
}
