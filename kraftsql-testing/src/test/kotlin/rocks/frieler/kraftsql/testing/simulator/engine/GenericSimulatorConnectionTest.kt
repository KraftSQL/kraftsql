package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table

class GenericSimulatorConnectionTest {
    private val connection = GenericSimulatorConnection<DummyEngine>()

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
    fun `GenericSimulatorConnection can simulate InsertInto`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT),
        )).also { connection.execute(CreateTable(it)) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c" to "foo"))

        val rows = connection.execute(InsertInto(table, testData))

        rows shouldBe 1
    }

    @Test
    fun `GenericSimulatorConnection rejects inserting data that does not match the Table's schema`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c1", DummyEngine.Types.TEXT),
            rocks.frieler.kraftsql.objects.Column("c2", DummyEngine.Types.TEXT),
        )).also { connection.execute(CreateTable(it)) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c1" to null, "col" to null))

        shouldThrow<IllegalArgumentException> {
            connection.execute(InsertInto(table, testData))
        }
    }

    @Test
    fun `GenericSimulatorConnection rejects inserting NULL into non-nullable column`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT, nullable = false),
        )).also { connection.execute(CreateTable(it)) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c" to null))

        shouldThrow<IllegalArgumentException> {
            connection.execute(InsertInto(table, testData))
        }
    }
}
