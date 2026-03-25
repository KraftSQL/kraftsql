package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table

class TransactionStateOverlayTest {
    private val parentState = mock<EngineState<DummyEngine>>()
    private val overlay = TransactionStateOverlay(parentState)

    @Test
    fun `containsTable() returns false for a non-existent table`() {
        whenever(parentState.containsTable("nonexistent_table")).thenReturn(false)

        val containsTable = overlay.containsTable("nonexistent_table")

        containsTable shouldBe false
    }

    @Test
    fun `containsTable() returns true for an existing table in the parent state`() {
        whenever(parentState.containsTable("existing_table")).thenReturn(true)

        val containsTable = overlay.containsTable("existing_table")

        containsTable shouldBe true
    }

    @Test
    fun `findTable() cannot find non-existent table`() {
        whenever(parentState.findTable("nonexistent_table")).thenReturn(null)

        val table = overlay.findTable("nonexistent_table")

        table shouldBe null
    }

    @Test
    fun `findTable() finds existing table from the parent state`() {
        val tableAndContent = Pair(mock<Table<DummyEngine, *>>(), mutableListOf<DataRow>())
        whenever(parentState.findTable("existing_table")).thenReturn(tableAndContent)

        val table = overlay.findTable("existing_table")

        table shouldBe tableAndContent
    }

    @Test
    fun `ensureTableCopy() clones table from parent state`() {
        val tableAndContent = Pair(mock<Table<DummyEngine, *>>(), mutableListOf(mock<DataRow>()))
        whenever(parentState.findTable("table")).thenReturn(tableAndContent)

        overlay.ensureTableCopy("table")

        overlay.containsTable("table") shouldBe true
        overlay.findTable("table").shouldNotBeNull {
            first shouldBe tableAndContent.first
            second shouldContainExactly tableAndContent.second
            second shouldNotBeSameInstanceAs tableAndContent.second
        }
    }

    @Test
    fun `ensureTableCopy() does not clone table from parent state if there is already a copy`() {
        val tableAndContent = Pair(mock<Table<DummyEngine, *>>(), mutableListOf(mock<DataRow>()))
        whenever(parentState.findTable("table")).thenReturn(tableAndContent)

        overlay.ensureTableCopy("table")
        val tableCopy = overlay.getTable("table")

        overlay.ensureTableCopy("table")
        overlay.getTable("table") shouldBeSameInstanceAs tableCopy
    }

    @Test
    fun `commitIntoParent() writes copied tables back to parent state`() {
        val initialRow = mock<DataRow>()
        val tableAndContent = Pair(mock<Table<DummyEngine, *>>(), mutableListOf(initialRow))
        whenever(parentState.findTable("table")).thenReturn(tableAndContent)

        overlay.ensureTableCopy("table")
        val addedRow = mock<DataRow>()
        overlay.getTable("table").second.add(addedRow)
        overlay.commitIntoParent()

        verify(parentState).writeTable(tableAndContent.first, listOf(initialRow, addedRow))
    }
}
