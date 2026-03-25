package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import kotlin.reflect.KClass

/**
 * Interface for a simulator of an [Engine].
 *
 * Such an [EngineSimulator] can execute all [rocks.frieler.kraftsql.commands.Command]s implemented by the [Engine].
 *
 * The [rocks.frieler.kraftsql.commands.Command]s are executed in the context of a [Connection], which may influence the
 * behavior by its configuration, previous commands, etc.
 *
 * @param E the [Engine] to simulate
 */
interface EngineSimulator<E : Engine<E>> {
    context(connection: Connection<E>)
    fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T>

    context(connection: Connection<E>)
    fun execute(createTable: CreateTable<E>)

    context(connection: Connection<E>)
    fun execute(dropTable: DropTable<E>)

    context(connection: Connection<E>)
    fun execute(insertInto: InsertInto<E, *>): Int

    context(connection: Connection<E>)
    fun execute(delete: Delete<E>): Int

    context(connection: Connection<E>)
    fun execute(beginTransaction: BeginTransaction<E>)

    context(connection: Connection<E>)
    fun execute(commitTransaction: CommitTransaction<E>)

    context(connection: Connection<E>)
    fun execute(rollbackTransaction: RollbackTransaction<E>)
}
