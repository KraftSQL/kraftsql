package rocks.frieler.kraftsql.data

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DataTest {
    private data class Item(val id: Int, val name: String?, val unknown: Int)

    private val data = object : Data<Item> {
        override val schema = listOf(Column("id", Type.NULLABLE_INTEGER), Column("name", Type.NULLABLE_STRING))
    }

    @Test
    fun `get operator returns Column expression for name of a column in the schema`() {
        val column = data.get<String?>("name")

        column.data shouldBe data
        column.name shouldBe "name"
        column.type shouldBe Type.NULLABLE_STRING
    }

    @Test
    fun `get operator rejects to provide Column expression for name of a column not in the schema`() {
        shouldThrow<IllegalArgumentException> {
            data.get<String?>("unknown")
        }
    }

    @Test
    fun `get operator returns Column expression for property of the rows' type`() {
        val column = data[Item::name]

        column.data shouldBe data
        column.name shouldBe "name"
        column.type shouldBe Type.NULLABLE_STRING
    }

    @Test
    fun `get operator rejects to provide Column expression for property that is not a column in the schema`() {
        shouldThrow<IllegalArgumentException> {
            data[Item::unknown]
        }
        shouldThrow<IllegalArgumentException> {
            data[Item::id]
        }
    }
}
