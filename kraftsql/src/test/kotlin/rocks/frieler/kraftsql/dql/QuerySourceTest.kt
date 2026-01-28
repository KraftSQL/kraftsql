package rocks.frieler.kraftsql.dql

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.DataRow

class QuerySourceTest {
    private val data = mock<Data<TestableDummyEngine, DataRow>>()

    @Test
    fun `sql() returns the data's sql`() {
        whenever(data.sql()).thenReturn("data")

        QuerySource(data).sql() shouldBe "data"
    }

    @Test
    fun `sql() wraps subqueries in parentheses`() {
        val command = mock<Data<TestableDummyEngine, DataRow>>(extraInterfaces = arrayOf(Command::class))
        whenever(command.sql()).thenReturn("SELECT * FROM table")

        QuerySource(command).sql() shouldBe "(SELECT * FROM table)"
    }

    @Test
    fun `sql() wraps sub-SELECT for ConstantData in parentheses`() {
        val constantData = mock<ConstantData<TestableDummyEngine, DataRow>>()
        whenever(constantData.sql()).thenReturn("SELECT 'hello' AS `message`")

        QuerySource(constantData).sql() shouldBe "(SELECT 'hello' AS `message`)"
    }

    @Test
    fun `sql() adds alias if set`() {
        whenever(data.sql()).thenReturn("data")

        QuerySource(data, "d").sql() shouldBe "data AS `d`"
    }


    @Test
    fun `column names are forwarded from underlying data`() {
        whenever(data.columnNames).thenReturn(listOf("c1", "c2"))

        QuerySource(data).columnNames shouldBe data.columnNames
    }

    @Test
    fun `column names are prefixed with the alias`() {
        whenever(data.columnNames).thenReturn(listOf("c1"))

        QuerySource(data, "a").columnNames shouldBe listOf("a.c1")
    }

    @Test
    fun `empty column name is replaced by alias`() {
        whenever(data.columnNames).thenReturn(listOf(""))

        QuerySource(data, "a").columnNames shouldBe listOf("a")
    }

    @Test
    fun `get operator provides column expression by name from underlying data`() {
        val column = mock<Column<TestableDummyEngine, Any?>> { whenever(it.name).thenReturn("c1") }
        whenever(data.columnNames).thenReturn(listOf("c1"))
        whenever(data["c1"]).thenReturn(column)

        QuerySource(data)["c1"] shouldBe column
    }

    @Test
    fun `get operator provides column expression by name prefixed with the alias`() {
        val column = mock<Column<TestableDummyEngine, Any?>> {
            whenever(it.name).thenReturn("c1")
        }
        whenever(data.columnNames).thenReturn(listOf("c1"))
        whenever(data["c1"]).thenReturn(column)
        whenever(column.withQualifier("a")).thenReturn(Column(listOf("a"), "c1"))

        val prefixedColumn = QuerySource(data, "a")["c1"]

        prefixedColumn.qualifiers shouldBe listOf("a")
        prefixedColumn.name shouldBe column.name
    }

    @Test
    fun `get operator provides column expression by qualified name prefixed with the alias`() {
        val column = mock<Column<TestableDummyEngine, Any?>> {
            whenever(it.name).thenReturn("c1")
        }
        whenever(data.columnNames).thenReturn(listOf("c1"))
        whenever(data["c1"]).thenReturn(column)
        whenever(column.withQualifier("a")).thenReturn(Column(listOf("a"), "c1"))

        val prefixedColumn = QuerySource(data, "a")["a.c1"]

        prefixedColumn.qualifiers shouldBe listOf("a")
        prefixedColumn.name shouldBe column.name
    }

    @Test
    fun `get operator provides only column like alias when underlying data has only one column with empty name`() {
        whenever(data.columnNames).thenReturn(listOf(""))

        val column = QuerySource(data, "a")["a"]

        column shouldBe Column("a")
    }
}
