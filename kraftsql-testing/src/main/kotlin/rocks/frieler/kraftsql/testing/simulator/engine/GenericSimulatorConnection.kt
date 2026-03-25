package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table
import rocks.frieler.kraftsql.dql.Select
import kotlin.reflect.KClass

/**
 * Generic [SimulatorConnection], that "connects" to a [GenericEngineSimulator].
 *
 * @param E the [Engine] to simulate
 * @param engine the [GenericEngineSimulator] to "connect" to
 */
open class GenericSimulatorConnection<E : Engine<E>>(
    protected open val engine: GenericEngineSimulator<E> = GenericEngineSimulator(),
) : SimulatorConnection<E> {
    override fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T> =
        engine.execute(select, type)

    override fun execute(createTable: CreateTable<E>) {
        engine.execute(createTable)
    }

    override fun execute(dropTable: DropTable<E>) {
        engine.execute(dropTable)
    }

    override fun execute(insertInto: InsertInto<E, *>): Int =
        engine.execute(insertInto)

    override fun execute(delete: Delete<E>): Int =
        engine.execute(delete)

    override fun execute(beginTransaction: BeginTransaction<E>) {
        engine.execute(beginTransaction)
    }

    override fun execute(commitTransaction: CommitTransaction<E>) {
        engine.execute(commitTransaction)
    }

    override fun execute(rollbackTransaction: RollbackTransaction<E>) {
        engine.execute(rollbackTransaction)
    }
}
