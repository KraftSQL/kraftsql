package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.objects.DataRow

class GenericSimulatorConnectionTest {
    private val engine = mock<GenericEngineSimulator<DummyEngine>>()
    private val connection = GenericSimulatorConnection(engine = engine)

    @Test
    fun `execute() forwards Select to engine and returns its result`() {
        val select = mock<Select<DummyEngine, DataRow>>()
        val data = listOf<DataRow>(mock())
        whenever(context(connection) { engine.execute(select, DataRow::class) })
            .thenReturn(data)

        val result = connection.execute(select, DataRow::class)

        result shouldBe data
    }

    @Test
    fun `execute() forwards CreateTable to engine`() {
        val createTable = mock<CreateTable<DummyEngine>>()

        connection.execute(createTable)

        context(connection) { verify(engine).execute(createTable) }
    }

    @Test
    fun `execute() forwards DropTable to engine`() {
        val dropTable = mock<DropTable<DummyEngine>>()

        connection.execute(dropTable)

        context(connection) { verify(engine).execute(dropTable) }
    }

    @Test
    fun `execute() forwards InsertInto to engine and returns its result`() {
        val insertInto = mock<InsertInto<DummyEngine, DataRow>>()
        whenever(context(connection) { engine.execute(insertInto) })
            .thenReturn(5)

        val result = connection.execute(insertInto)

        result shouldBe 5
    }

    @Test
    fun `execute() forwards Delete to engine and returns its result`() {
        val delete = mock<Delete<DummyEngine>>()
        whenever(context(connection) { engine.execute(delete) })
            .thenReturn(3)

        val result = connection.execute(delete)

        result shouldBe 3
    }

    @Test
    fun `execute() forwards BeginTransaction to engine`() {
        val beginTransaction = mock<BeginTransaction<DummyEngine>>()

        connection.execute(beginTransaction)

        context(connection) { verify(engine).execute(beginTransaction) }
    }

    @Test
    fun `execute() forwards CommitTransaction to engine`() {
        val commitTransaction = mock<CommitTransaction<DummyEngine>>()

        connection.execute(commitTransaction)

        context(connection) { verify(engine).execute(commitTransaction) }
    }

    @Test
    fun `execute() forwards RollbackTransaction to engine`() {
        val rollbackTransaction = mock<RollbackTransaction<DummyEngine>>()

        connection.execute(rollbackTransaction)

        context(connection) { verify(engine).execute(rollbackTransaction) }
    }
}
