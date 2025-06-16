package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.queries.Select
import java.sql.ResultSet
import kotlin.reflect.KClass

interface Session<E: Engine<E>> {
    fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T>

    fun execute(createTable: CreateTable<E>)

    fun execute(insertInto: InsertInto<E, *>) : Int
}

inline fun <E : Engine<E>, reified T : Any> Session<E>.execute(select: Select<E, T>) = execute(select, T::class)
