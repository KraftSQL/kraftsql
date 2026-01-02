package rocks.frieler.kraftsql.objects

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DataRowTest {
    @Test
    fun `DataRow rejects duplicate column names`() {
        shouldThrow<IllegalArgumentException> {
            DataRow("col" to "foo", "col" to "bar")
        }
    }

    @Test
    fun `DataRow provides column names in order`() {
        val row = DataRow("c1" to "foo", "c2" to "bar")

        row.columnNames shouldBe listOf("c1", "c2")
    }

    @Test
    fun `get returns value from column by name`() {
        val row = DataRow("c1" to "foo", "c2" to "bar")

        row["c1"] shouldBe "foo"
        row["c2"] shouldBe "bar"
    }

    @Test
    fun `get throws exception suggesting column names if requested column does not exist`() {
        val row = DataRow("c1" to "foo")

        shouldThrow<IllegalStateException> {
            row["c2"]
        }.apply {
            message shouldBe "No field 'c2' in DataRow; did you mean one of [c1]?"
        }
    }

    @Test
    fun `get returns value from qualified column by name`() {
        val row = DataRow("x.col" to "foo")

        row["x.col"] shouldBe "foo"
    }

    @Test
    fun `get returns value from subfield`() {
        val row = DataRow("row" to DataRow("col" to "foo"))

        row["row.col"] shouldBe "foo"
    }

    @Test
    fun `get throws exception when requesting value from subfield which is not a DataRow`() {
        val row = DataRow("foo" to "bar")

        shouldThrow<IllegalStateException> {
            row["foo.x"]
        }
    }

    @Test
    fun `plus concatenates to DataRows`() {
        DataRow("c1" to "foo") + DataRow("c2" to "bar") shouldBe DataRow("c1" to "foo", "c2" to "bar")
    }

    @Test
    fun `toString renders entries as key-value-pairs`() {
        DataRow("c1" to "foo", "c2" to "bar").toString() shouldBe "DataRow(c1=foo, c2=bar)"
    }

    @Test
    fun `DataRows with equal schema and values are not equal`() {
        (DataRow("column" to "foo") == DataRow("column" to "foo")) shouldBe true
    }

    @Test
    fun `DataRows with different schema are not equal`() {
        (DataRow("column" to "foo") == DataRow("field" to "foo")) shouldBe false
    }

    @Test
    fun `DataRows with different values are not equal`() {
        (DataRow("column" to "foo") == DataRow("column" to "bar")) shouldBe false
    }

    @Test
    fun `equal DataRows have same hash code`() {
        val dataRow = DataRow("column" to "foo")
        DataRow(dataRow.entries).hashCode() shouldBe dataRow.hashCode()
    }
}
