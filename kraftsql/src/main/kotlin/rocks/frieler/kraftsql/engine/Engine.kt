package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.queries.Select
import java.sql.ResultSet
import kotlin.reflect.KType

interface Engine<E : Engine<E>> {
    fun getTypeFor(type: KType): Type

    fun <T : Any> execute(select: Select<E, T>): ResultSet

    fun execute(createTable: CreateTable<E>)

    fun execute(insertInto: InsertInto<E, *>) : Int
}
