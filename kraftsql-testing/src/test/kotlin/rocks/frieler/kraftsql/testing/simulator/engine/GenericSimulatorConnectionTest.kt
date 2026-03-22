package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table

class GenericSimulatorConnectionTest {
    private val engine = GenericEngineSimulator<DummyEngine>()
    private val connection = GenericSimulatorConnection(
        engine = engine,
    )

    @Test
    fun `GenericSimulatorConnection can simulate Select`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(Constant(42L))),
            ), DataRow::class
        )

        result.single().entries.single().second shouldBe 42L
    }

    @Test
    fun `GenericSimulatorConnection can simulate CreateTable`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT),
        ))

        connection.execute(CreateTable(table))

        engine.persistentState.findTable(table.qualifiedName) shouldNotBe null
    }

    @Test
    fun `GenericSimulatorConnection can simulate DropTable`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT),
        )).also { engine.persistentState.addTable(it) }

        connection.execute(DropTable(table))

        engine.persistentState.findTable(table.qualifiedName) shouldBe null
    }

    @Test
    fun `GenericSimulatorConnection can simulate InsertInto`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT),
        )).also { engine.persistentState.addTable(it) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c" to "foo"))

        val rows = connection.execute(InsertInto(table, testData))

        rows shouldBe 1
        engine.persistentState.getTable(table.qualifiedName).second.shouldContainAllInAnyOrder(testData.items.toList())
    }

    @Test
    fun `GenericSimulatorConnection rejects inserting data that does not match the Table's schema`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c1", DummyEngine.Types.TEXT),
            rocks.frieler.kraftsql.objects.Column("c2", DummyEngine.Types.TEXT),
        )).also { engine.persistentState.addTable(it) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c1" to null, "col" to null))

        shouldThrow<IllegalArgumentException> {
            connection.execute(InsertInto(table, testData))
        }
    }

    @Test
    fun `GenericSimulatorConnection rejects inserting NULL into non-nullable column`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT, nullable = false),
        )).also { engine.persistentState.addTable(it) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c" to null))

        shouldThrow<IllegalArgumentException> {
            connection.execute(InsertInto(table, testData))
        }
    }

    @Test
    fun `GenericSimulatorConnection can simulate Delete`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT, nullable = false),
        )).also {
            engine.persistentState.addTable(it)
            engine.persistentState.writeTable(it, listOf(DataRow("c" to "foo"), DataRow("c" to "bar")))
        }

        val rowsDeleted = connection.execute(Delete(table, table["c"] `=` Constant("foo")))

        rowsDeleted shouldBe 1
        engine.persistentState.getTable(table.qualifiedName).second.shouldHaveSize(1)
    }

    @Test
    fun `GenericSimulatorConnection deletes all rows with an unconditioned Delete`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT, nullable = false),
        )).also {
            engine.persistentState.addTable(it)
            engine.persistentState.writeTable(it, listOf(DataRow("c" to "foo"), DataRow("c" to "bar")))
        }

        val rowsDeleted = connection.execute(Delete(table))

        rowsDeleted shouldBe 2
        engine.persistentState.getTable(table.qualifiedName).second.shouldBeEmpty()
    }
}
