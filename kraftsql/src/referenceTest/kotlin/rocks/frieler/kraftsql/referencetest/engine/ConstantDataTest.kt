package rocks.frieler.kraftsql.referencetest.engine

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.data.ConstantData
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.DataRow
import rocks.frieler.kraftsql.engine.collect

class ConstantDataTest {
    @Test
    fun `Engine can collect ConstantData of DataRows`() {
        val dataRows = listOf(DataRow("answer" to 42))

        val collected = ConstantData(dataRows).collect()

        collected shouldBe dataRows
    }

    @Test
    fun `Engine can collect ConstantData of DataRows as data-class instances`() {
        data class Answer(val answer: Int)
        val item = DataRow("answer" to 42)
        @Suppress("UNCHECKED_CAST") // TODO: use cast() function later
        val data = ConstantData(item) as Data<Answer>

        val collectedData = data.collect<Answer>()

        collectedData shouldContainExactly listOf(Answer(42))
    }

    @Test
    fun `Engine can collect ConstantData of data-class instances as DataRows`() {
        data class Answer(val answer: Int)
        val item = Answer(42)
        @Suppress("UNCHECKED_CAST") // TODO: use cast() function later
        val data = ConstantData(item) as Data<DataRow>

        val collectedData = data.collect<DataRow>()

        collectedData shouldContainExactly listOf(DataRow("answer" to 42))
    }
}
