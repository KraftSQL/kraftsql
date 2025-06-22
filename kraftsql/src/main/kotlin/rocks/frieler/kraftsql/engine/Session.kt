package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.queries.Select
import kotlin.reflect.KClass

interface Session<E: Engine<E>> {
    fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T>

    fun execute(createTable: CreateTable<E>)

    fun execute(insertInto: InsertInto<E, *>) : Int
}

abstract class DefaultSession<E : Engine<E>> {
    private lateinit var instance : Session<E>

    fun get(): Session<E> {
        if (!::instance.isInitialized) {
            instance = instantiate()
        }

        return instance
    }

    protected abstract fun instantiate(): Session<E>

    fun set(instance: Session<E>) {
        this.instance = instance
    }
}
