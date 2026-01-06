package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

/**
 * An [Expression] that aggregates that calculates an aggregate value over multiple rows.
 *
 * @param <E> the [Engine] that implements and executes this [Aggregation]
 * @param <T> the Kotlin type of the [Aggregation]'s result value
 */
interface Aggregation<E: Engine<E>, out T> : Expression<E, T>
