package rocks.frieler.kraftsql.objects

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class TableTest {
    private val table = Table<TestableDummyEngine, Any>(
        name = "t",
        columns = listOf(
            mock<Column<TestableDummyEngine>> { whenever(it.name).thenReturn("c1") },
            mock<Column<TestableDummyEngine>> { whenever(it.name).thenReturn("c2") },
        )
    )

    @Test
    fun `columnNames provides the names of the columns`() {

        table.columnNames shouldBe listOf("c1", "c2")
    }

    @Test
    fun `get operator provides existing column by name`() {
        val column = table["c1"]

        column.shouldBeInstanceOf<rocks.frieler.kraftsql.expressions.Column<TestableDummyEngine, *>>()
        column.name shouldBe "c1"
    }

    @Test
    fun `get operator rejects to provide non-existent column by name`() {
        shouldThrow<IllegalStateException> {
            table["foo"]
        }
    }
}
