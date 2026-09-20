package rocks.frieler.kraftsql.data

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DataRowTest {
    @Test
    fun `DataRow rejects duplicate field names`() {
        shouldThrow<IllegalArgumentException> {
            DataRow("col" to "foo", "col" to "bar")
        }
    }

    @Test
    fun `DataRow provides field names in order`() {
        val row = DataRow("c1" to "foo", "c2" to "bar")

        row.fieldNames shouldBe listOf("c1", "c2")
    }

    @Test
    fun `get returns value from field by name`() {
        val row = DataRow("c1" to "foo", "c2" to "bar")

        row["c1"] shouldBe "foo"
        row["c2"] shouldBe "bar"
    }

    @Test
    fun `get throws exception suggesting column names if requested field does not exist`() {
        val row = DataRow("c1" to "foo")

        shouldThrow<IllegalStateException> {
            row["c2"]
        }.apply {
            message shouldBe "No field 'c2' in DataRow; did you mean one of [c1]?"
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
    fun `toString renders array entry elements`() {
        DataRow("c" to arrayOf("foo", "bar")).toString() shouldBe "DataRow(c=[foo, bar])"
    }

    @Test
    fun `DataRow and something else are not equal`() {
        (DataRow("field" to "foo") == Any()) shouldBe false
    }

    @Test
    fun `DataRows with equal schema and values are equal`() {
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
