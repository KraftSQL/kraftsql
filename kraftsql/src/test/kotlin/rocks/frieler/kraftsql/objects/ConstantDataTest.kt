package rocks.frieler.kraftsql.objects

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.ORMapping
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Row

class ConstantDataTest {
    private val orm = mock<ORMapping<TestableDummyEngine, *>>()

    @Test
    fun `ConstantData can hold items`() {
        val item1 = mock<DataRow> { whenever(it.columnNames).thenReturn(listOf("c1", "c2")) }
        val item2 = mock<DataRow> { whenever(it.columnNames).thenReturn(listOf("c1", "c2")) }

        val data = ConstantData(orm, listOf(item1, item2))

        data.items shouldBe listOf(item1, item2)
    }

    @Test
    fun `ConstantData rejects empty items`() {
        shouldThrow<IllegalArgumentException> {
            ConstantData(orm, emptyList())
        }
    }

    @Test
    fun `ConstantData rejects items of different types`() {
        shouldThrow<IllegalArgumentException> {
            ConstantData(orm, listOf(mock<DataRow>(), mock<Any>()))
        }
    }

    @Test
    fun `ConstantData rejects DataRow items with different columns`() {
        val item1 = mock<DataRow> { whenever(it.columnNames).thenReturn(listOf("c1", "c2")) }
        val item2 = mock<DataRow> { whenever(it.columnNames).thenReturn(listOf("x", "y")) }

        shouldThrow<IllegalArgumentException> {
            ConstantData(orm, listOf(item1, item2))
        }
    }

    @Test
    fun `ConstantData can also be created from vararg items`() {
        val item1 = mock<DataRow>()
        val item2 = mock<DataRow>()

        val data = ConstantData(orm, item1, item2)

        data.items shouldBe listOf(item1, item2)
    }

    @Test
    fun `empty ConstantData can be created with column names`() {
        val emptyData = ConstantData.empty<TestableDummyEngine, DataRow>(orm, listOf("c1", "c2"))

        emptyData.items shouldBe emptyList()
    }

    @Test
    fun `empty ConstantData can be created deriving schema from data class`() {
        data class Item(val id: Int, val value: String)
        whenever(orm.getSchemaFor(Item::class)).thenReturn(listOf(
            Column("id", TestableDummyEngine.Types.INTEGER),
            Column("value", TestableDummyEngine.Types.TEXT),
        ))

        val emptyData = ConstantData.empty<TestableDummyEngine, Item>(orm)

        emptyData.items shouldBe emptyList()
    }

    @Test
    fun `SQL selects empty data of item schema`() {
        val emptyData = ConstantData.empty<TestableDummyEngine, DataRow>(orm, listOf("c1", "c2"))

        emptyData.sql() shouldBe "SELECT NULL AS `c1`, NULL AS `c2` WHERE FALSE"
    }

    @Test
    fun `SQL selects union constant rows`() {
        val item1 = mock<DataRow> {
            whenever(it.columnNames).thenReturn(listOf("c1", "c2"))
            whenever(orm.serialize(it)).thenReturn(Row(mapOf("c1" to Constant(1), "c2" to Constant("hello"))))
        }
        val item2 = mock<DataRow> {
            whenever(it.columnNames).thenReturn(listOf("c1", "c2"))
            whenever(orm.serialize(it)).thenReturn(Row(mapOf("c1" to Constant(2), "c2" to Constant("world"))))
        }

        val data = ConstantData(orm, item1, item2)

        data.sql() shouldBe """SELECT 1 AS `c1`, 'hello' AS `c2` UNION ALL SELECT 2 AS `c1`, 'world' AS `c2`"""
    }

    @Test
    fun `SQL cannot be generated when ORM does not serialize item to Row`() {
        val item = mock<DataRow> { whenever(it.columnNames).thenReturn(listOf("c1")) }
        whenever(orm.serialize(item)).thenReturn(mock())
        val data = ConstantData(orm, item)

        shouldThrow<IllegalStateException> {
            data.sql()
        }
    }
}
