package rocks.frieler.kraftsql.engine

/**
 * **Kraft**SQL's abstraction of an SQL engine.
 *
 * The interface itself is empty. Subtypes are used as type parameters to mark SQL stuff that runs on the same [Engine]
 * and is thereby combinable in a statement.
 *
 * @param <E> compatible [Engine]s, usually the [Engine] itself
 */
interface Engine<E : Engine<E>>
