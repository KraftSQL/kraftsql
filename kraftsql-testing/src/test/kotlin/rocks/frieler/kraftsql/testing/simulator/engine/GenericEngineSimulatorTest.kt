package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table

class GenericEngineSimulatorTest {
    private val simulator = object : GenericEngineSimulator<DummyEngine>() {
        public override val persistentState = super.persistentState
    }

    private val connection = mock<Connection<DummyEngine>>()

    @Test
    fun `GenericEngineSimulator requires QueryEvaluator to use the same ExpressionEvaluator`() {
        shouldThrow<IllegalArgumentException> {
            GenericEngineSimulator<DummyEngine>(
                mock(),
                mock(),
                mock(),
                mock { whenever(it.expressionEvaluatorForChecking).thenReturn(mock()) },
            )
        }
    }

    @Test
    fun `GenericEngineSimulator can simulate Select`() {
        val result = context(connection) { simulator.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(Constant(42L))),
            ), DataRow::class
        ) }

        result.single().entries.single().second shouldBe 42L
    }

    @Test
    fun `GenericEngineSimulator can simulate CreateTable`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT),
        ))

        context(connection) { simulator.execute(CreateTable(table)) }

        simulator.persistentState.findTable(table.qualifiedName) shouldNotBe null
    }

    @Test
    fun `GenericEngineSimulator can simulate DropTable`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT),
        )).also { simulator.persistentState.addTable(it) }

        context(connection) { simulator.execute(DropTable(table)) }

        simulator.persistentState.findTable(table.qualifiedName) shouldBe null
    }

    @Test
    fun `GenericEngineSimulator can simulate InsertInto`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT),
        )).also { simulator.persistentState.addTable(it) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c" to "foo"))

        val rows = context(connection) {
            simulator.execute(InsertInto(table, testData))
        }

        rows shouldBe 1
        simulator.persistentState.getTable(table.qualifiedName).second.shouldContainAllInAnyOrder(testData.items.toList())
    }

    @Test
    fun `GenericEngineSimulator rejects inserting data that does not match the Table's schema`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c1", DummyEngine.Types.TEXT),
            rocks.frieler.kraftsql.objects.Column("c2", DummyEngine.Types.TEXT),
        )).also { simulator.persistentState.addTable(it) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c1" to null, "col" to null))

        shouldThrow<IllegalArgumentException> {
            context(connection) { simulator.execute(InsertInto(table, testData)) }
        }
    }

    @Test
    fun `GenericEngineSimulator rejects inserting NULL into non-nullable column`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT, nullable = false),
        )).also { simulator.persistentState.addTable(it) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c" to null))

        shouldThrow<IllegalArgumentException> {
            context(connection) { simulator.execute(InsertInto(table, testData)) }
        }
    }

    @Test
    fun `GenericEngineSimulator can simulate Delete`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT, nullable = false),
        )).also {
            simulator.persistentState.addTable(it)
            simulator.persistentState.writeTable(it, listOf(DataRow("c" to "foo"), DataRow("c" to "bar")))
        }

        val rowsDeleted = context(connection) {
            simulator.execute(Delete(table, table["c"] `=` Constant("foo")))
        }

        rowsDeleted shouldBe 1
        simulator.persistentState.getTable(table.qualifiedName).second.shouldHaveSize(1)
    }

    @Test
    fun `GenericEngineSimulator deletes all rows with an unconditioned Delete`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT, nullable = false),
        )).also {
            simulator.persistentState.addTable(it)
            simulator.persistentState.writeTable(it, listOf(DataRow("c" to "foo"), DataRow("c" to "bar")))
        }

        val rowsDeleted = context(connection) {
            simulator.execute(Delete(table))
        }

        rowsDeleted shouldBe 2
        simulator.persistentState.getTable(table.qualifiedName).second.shouldBeEmpty()
    }
}
