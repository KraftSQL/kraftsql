package rocks.frieler.kraftsql.local.engine

import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.data.ConstantData
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.DataRow

class KraftSqlLocalEngineTest {
    private val engine = KraftSqlLocalEngine()

    @Test
    fun `collect() can collect ConstantData of DataRows as DataRows`() {
        val items = listOf(mockk<DataRow>(), mockk<DataRow>())
        val data = mockk<ConstantData<DataRow>> { every { this@mockk.items } returns items }

        val collectedData = engine.collect(data, DataRow::class)

        collectedData shouldContainExactly items
    }

    @Test
    fun `collect() can collect ConstantData of DataRows as data-class instances`() {
        data class Answer(val answer: Int)
        val item = DataRow("answer" to 42)
        @Suppress("UNCHECKED_CAST") val data = mockk<ConstantData<DataRow>> { every { this@mockk.items } returns listOf(item) } as Data<Answer>

        val collectedData = engine.collect(data, Answer::class)

        collectedData shouldContainExactly listOf(Answer(42))
    }

    @Test
    fun `collect() can collect ConstantData of data-class instances as DataRows`() {
        data class Answer(val answer: Int)
        val item = Answer(42)
        @Suppress("UNCHECKED_CAST") val data = mockk<ConstantData<Answer>> { every { this@mockk.items } returns listOf(item) } as Data<DataRow>

        val collectedData = engine.collect(data, DataRow::class)

        collectedData shouldContainExactly listOf(DataRow("answer" to 42))
    }
}
