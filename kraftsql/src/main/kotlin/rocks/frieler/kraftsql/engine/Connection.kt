package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.dql.Select
import kotlin.reflect.KClass

interface Connection<E: Engine<E>> {
    fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T>

    fun execute(createTable: CreateTable<E>)

    fun execute(dropTable: DropTable<E>)

    fun execute(insertInto: InsertInto<E, *>) : Int

    fun execute(delete: Delete<E>) : Int

    fun execute(beginTransaction: BeginTransaction<E>)

    fun execute(commitTransaction: CommitTransaction<E>)

    fun execute(rollbackTransaction: RollbackTransaction<E>)
}

abstract class DefaultConnection<E : Engine<E>> {
    private var instance : Connection<E>? = null

    fun get(): Connection<E> = (instance ?: instantiate()).also { instance = it }

    protected abstract fun instantiate(): Connection<E>

    fun set(instance: Connection<E>) {
        this.instance = instance
    }

    fun unset() {
        this.instance = null
    }
}
