package rocks.frieler.kraftsql.data

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.expressions.Column as ColumnExpression

class SelectionTest {
    private val source = mockk<Data<*>> {
        every { schema } returns listOf(Column("id", Type.INTEGER), Column("name", Type.NULLABLE_STRING))
    }
    private val id = ColumnExpression<Int>(source, "id")
    private val name = ColumnExpression<String?>(source, "name")

    @Test
    fun `Selection can select columns of the source`() {
        val selection = Selection(source, listOf(id, name))

        selection.source shouldBe source
        selection.expressions shouldBe listOf(id, name)
    }

    @Test
    fun `Selection rejects an empty list of columns`() {
        shouldThrow<IllegalArgumentException> {
            Selection(source, emptyList())
        }
    }

    @Test
    fun `Selection rejects a column selected more than once`() {
        shouldThrow<IllegalArgumentException> {
            Selection(source, listOf(id, name, ColumnExpression<Int>(source, "id")))
        }
    }

    @Test
    fun `Selection rejects columns of other Data than the source`() {
        val otherData = mockk<Data<*>> {
            every { schema } returns listOf(Column("id", Type.INTEGER))
        }
        val selection = Selection(source, listOf(id, name))

        shouldThrow<IllegalArgumentException> {
            Selection(source, listOf(ColumnExpression<Int>(otherData, "id")))
        }
        shouldThrow<IllegalArgumentException> {
            Selection(selection, listOf(id))
        }
    }

    @Test
    fun `Selection has the names and types of the selected columns as schema in the order of the selection`() {
        val selection = Selection(source, listOf(name, id))

        selection.schema shouldBe listOf(Column("name", Type.NULLABLE_STRING), Column("id", Type.INTEGER))
    }

    @Test
    fun `select() creates a Selection of the given columns from the Data`() {
        val selection = source.select(listOf(id, name))

        selection.shouldBeInstanceOf<Selection>()
        selection.source shouldBe source
        selection.expressions shouldBe listOf(id, name)
    }

    @Test
    fun `select() also accepts the columns as vararg`() {
        val selection = source.select(id, name)

        selection.shouldBeInstanceOf<Selection>()
        selection.source shouldBe source
        selection.expressions shouldBe listOf(id, name)
    }
}
