package rocks.frieler.kraftsql.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

class ConnectionTest {
    private val testableConnection = mock<Connection<TestableDummyEngine>> {
        whenever(it.collect(any<Data<TestableDummyEngine, DataRow>>(), any<KClass<DataRow>>())).thenCallRealMethod()
    }

    @Test
    fun `collect wraps arbitrary Data in Select command`() {
        val data = mock<Data<TestableDummyEngine, DataRow>>()
        val testData = listOf(
            DataRow("foo" to "bar"),
            DataRow("foo" to "baz"),
        )
        whenever(testableConnection.execute(Select(source = QuerySource(data)), DataRow::class)).thenReturn(testData)

        val collectedData = testableConnection.collect(data, DataRow::class)

        collectedData shouldBe testData
    }

    @Test
    fun `collect returns items from ConstantData immediately`() {
        val testData = listOf(
            DataRow("foo" to "bar"),
            DataRow("foo" to "baz"),
        )
        val constantData = ConstantData(TestableDummyEngine.orm, testData)

        val collectedData = testableConnection.collect(constantData, DataRow::class)

        collectedData shouldBe testData
        verify(testableConnection, never()).execute(any<Select<TestableDummyEngine, DataRow>>(), any())
    }

    @Test
    fun `collect executes Select command`() {
        val select = mock<Select<TestableDummyEngine, DataRow>>()
        val testData = listOf(
            DataRow("foo" to "bar"),
            DataRow("foo" to "baz"),
        )
        whenever(testableConnection.execute(select, DataRow::class)).thenReturn(testData)

        val collectedData = testableConnection.collect(select, DataRow::class)

        collectedData shouldBe testData
    }
}
