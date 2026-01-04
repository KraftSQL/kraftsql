package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.Data
import kotlin.reflect.KClass

/**
 * A connection to a SQL [Engine] to execute [rocks.frieler.kraftsql.commands.Command]s.
 *
 * @param E the SQL [Engine] to connect to
 */
interface Connection<E: Engine<E>> {
    fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T>

    /**
     * Collects the given [Data]'s rows as Kotlin objects.
     *
     * @param T the Kotlin type of the rows
     * @param data the [Data] to collect
     * @param type the [Kotlin class][KClass] of `T` to return the rows
     * @return the [Data]'s rows as Kotlin objects
     */
    fun <T : Any> collect(data: Data<E, T>, type: KClass<T>): List<T> =
        when (data) {
            is ConstantData<E, T> -> data.items.toList()
            is Select<E, T> -> execute(data, type)
            else -> execute(Select(source = QuerySource(data)), type)
        }

    fun execute(createTable: CreateTable<E>)

    fun execute(dropTable: DropTable<E>)

    fun execute(insertInto: InsertInto<E, *>) : Int

    fun execute(delete: Delete<E>) : Int

    fun execute(beginTransaction: BeginTransaction<E>)

    fun execute(commitTransaction: CommitTransaction<E>)

    fun execute(rollbackTransaction: RollbackTransaction<E>)
}

abstract class DefaultConnection<E : Engine<E>, C : Connection<E>> {
    private var instance : C? = null

    fun get(): C = (instance ?: instantiate()).also { instance = it }

    protected abstract fun instantiate(): C

    fun set(instance: C) {
        this.instance = instance
    }

    fun unset() {
        this.instance = null
    }
}
