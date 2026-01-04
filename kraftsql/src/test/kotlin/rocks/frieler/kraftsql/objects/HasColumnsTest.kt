package rocks.frieler.kraftsql.objects

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Column
import kotlin.reflect.KProperty1

class HasColumnsTest {
    private val testableHasColumnsInstance = mock<HasColumns<TestableDummyEngine, HasColumnsTest>> {
        whenever(it[anyString()]).thenCallRealMethod()
        whenever(it[any<KProperty1<HasColumnsTest, *>>()]).thenCallRealMethod()
    }

    @Test
    fun `get operator returns Column expression for name of existing column`() {
        whenever(testableHasColumnsInstance.columnNames).thenReturn(listOf("col"))

        val column = testableHasColumnsInstance["col"]

        column.shouldBeInstanceOf<Column<TestableDummyEngine, *>>()
        column.name shouldBe "col"
    }

    @Test
    fun `get operator rejects to provide Column expression for name of non-existent column`() {
        whenever(testableHasColumnsInstance.columnNames).thenReturn(listOf("foo"))

        shouldThrow<IllegalArgumentException> {
            testableHasColumnsInstance["bar"]
        }
    }

    @Test
    fun `get operator returns Column expression for property`() {
        val property = mock<KProperty1<HasColumnsTest, String>> { whenever(it.name).thenReturn("prop")}
        whenever(testableHasColumnsInstance.columnNames).thenReturn(listOf("prop"))

        val column = testableHasColumnsInstance[property]

        column.shouldBeInstanceOf<Column<TestableDummyEngine, String>>()
        column.name shouldBe "prop"
    }
}
