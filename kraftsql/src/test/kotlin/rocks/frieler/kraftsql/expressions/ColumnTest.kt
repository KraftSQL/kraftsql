package rocks.frieler.kraftsql.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class ColumnTest {
    private val column = Column<TestableDummyEngine, Int>(listOf("a"), "x")

    @Test
    fun `Column cannot provide possible sub-column names`() {
        shouldThrow<NotImplementedError> {
            column.columnNames
        }
    }

    @Test
    fun `get operator provides a sub-column qualified with this column's full qualified name`() {
        val subColumn = column["sub"]

        subColumn.shouldBeInstanceOf<Column<TestableDummyEngine, *>>()
        subColumn.qualifiers shouldBe column.qualifiers + listOf(column.name)
        subColumn.name shouldBe "sub"
    }
}
