package rocks.frieler.kraftsql.h2.engine

import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.data.Column
import rocks.frieler.kraftsql.data.ConstantData
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.DataRow
import rocks.frieler.kraftsql.data.Type

class H2EngineTest {
    private val engine = H2Engine()

    @Test
    fun `collect() can collect ConstantData of DataRows as DataRows`() {
        val items = listOf(DataRow("id" to 1), DataRow("id" to 2))
        val data = mockk<ConstantData<DataRow>> {
            every { this@mockk.items } returns items
            every { schema } returns listOf(Column("id", Type.INTEGER))
        }

        val collectedData = engine.collect(data, DataRow::class)

        collectedData shouldContainExactly items
    }

    @Test
    fun `collect() can collect ConstantData of DataRows as data-class instances`() {
        data class Answer(val answer: Int)
        val item = DataRow("answer" to 42)
        @Suppress("UNCHECKED_CAST") val data = mockk<ConstantData<DataRow>> {
            every { this@mockk.items } returns listOf(item)
            every { schema } returns listOf(Column("answer", Type.INTEGER))
        } as Data<Answer>

        val collectedData = engine.collect(data, Answer::class)

        collectedData shouldContainExactly listOf(Answer(42))
    }

    @Test
    fun `collect() can collect ConstantData of data-class instances as DataRows`() {
        data class Answer(val answer: Int)
        val item = Answer(42)
        @Suppress("UNCHECKED_CAST") val data = mockk<ConstantData<Answer>> {
            every { this@mockk.items } returns listOf(item)
            every { schema } returns listOf(Column("answer", Type.INTEGER))
        } as Data<DataRow>

        val collectedData = engine.collect(data, DataRow::class)

        collectedData shouldContainExactly listOf(DataRow("answer" to 42))
    }

    @Test
    fun `collect() can collect values of all supported types`() {
        val items = listOf(DataRow(
            "int" to 1,
            "null_as_int" to null,
            "string" to "KraftSQL",
            "null_as_string" to null,
        ))
        val data = mockk<ConstantData<DataRow>> {
            every { this@mockk.items } returns items
            every { schema } returns listOf(
                Column("int", Type.INTEGER),
                Column("null_as_int", Type.NULLABLE_INTEGER),
                Column("string", Type.STRING),
                Column("null_as_string", Type.NULLABLE_STRING),
            )
        }

        val collectedData = engine.collect(data, DataRow::class)

        collectedData shouldContainExactly items
    }

    @Test
    fun `collect() can collect special Strings`() {
        val items = listOf(DataRow(
            "string_with_quotes" to "it's \"quoted\"",
            "empty_string" to "",
        ))
        val data = mockk<ConstantData<DataRow>> {
            every { this@mockk.items } returns items
            every { schema } returns listOf(
                Column("string_with_quotes", Type.STRING),
                Column("empty_string", Type.STRING),
            )
        }

        val collectedData = engine.collect(data, DataRow::class)

        collectedData shouldContainExactly items
    }
}
