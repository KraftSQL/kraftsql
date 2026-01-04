package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine

/**
 * Base interface for all representations of relational data.
 *
 * Such a [Data] representation can be resolved against an [Engine], to work with that data, usually by rendering and
 * executing SQL code.
 *
 * @param <E> the [Engine] where this [Data] resides and can be worked with
 * @param <T> the Kotlin type of the [Data]'s rows
 */
interface Data<E : Engine<E>, T : Any> : HasColumns<E, T> {
    /**
     * Generates the SQL code for this [Data].
     *
     * @return the SQL code for this [Data]
     */
    fun sql(): String
}

/**
 * Collects this [Data]'s rows as Kotlin objects using the given [Connection].
 *
 * @param T the Kotlin type of the rows
 * @param connection the [Connection] to collect the data
 * @return the [Data]'s rows as Kotlin objects
 * @see Connection.collect
 */
inline fun <E : Engine<E>, reified T : Any> Data<E, T>.collect(connection: Connection<E>) =
    connection.collect(this, T::class)
