package rocks.frieler.kraftsql.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.Type
import rocks.frieler.kraftsql.data.Column as ColumnDefinition

class ColumnTest {
    private val data = mockk<Data<*>> {
        every { schema } returns listOf(ColumnDefinition("id", Type.INTEGER), ColumnDefinition("name", Type.NULLABLE_STRING))
    }
    private val otherData = mockk<Data<*>> {
        every { schema } returns listOf(ColumnDefinition("id", Type.INTEGER), ColumnDefinition("name", Type.NULLABLE_STRING))
    }

    @Test
    fun `Column can be constructed for the name of a column in the Data's schema`() {
        val column = Column<Int>(data, "id")

        column.data shouldBe data
        column.name shouldBe "id"
    }

    @Test
    fun `Column rejects the name of a column not in the Data's schema`() {
        shouldThrow<IllegalArgumentException> {
            Column<Int>(data, "ID")
        }.apply {
            message shouldBe "No column 'ID' in Data; did you mean one of [id, name]?"
        }
    }

    @Test
    fun `Column provides its type from the Data's schema`() {
        Column<Int>(data, "id").type shouldBe Type.INTEGER
        Column<String?>(data, "name").type shouldBe Type.NULLABLE_STRING
    }

    @Test
    fun `Column equals another Column of the same Data with the same name`() {
        Column<Int>(data, "id") shouldBe Column(data, "id")
    }

    @Test
    fun `Column does not equal a Column of the same Data with a different name`() {
        Column<Int>(data, "id") shouldNotBe Column<String?>(data, "name")
    }

    @Test
    fun `Column does not equal a Column of other Data with the same name`() {
        Column<Int>(data, "id") shouldNotBe Column<Int>(otherData, "id")
    }

    @Test
    fun `Column does not equal something that is not a Column`() {
        Column<Int>(data, "id") shouldNotBe "id"
    }

    @Test
    fun `Equal Columns have the same hashCode`() {
        Column<Int>(data, "id").hashCode() shouldBe Column<Int>(data, "id").hashCode()
    }

    @Test
    fun `Column's toString() returns its name`() {
        Column<Int>(data, "id").toString() shouldBe "id"
    }
}
