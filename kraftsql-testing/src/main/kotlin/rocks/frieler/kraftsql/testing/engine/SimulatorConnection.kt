package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine

/**
 * Marker interface for simulations of [Connection]s to be used in tests.
 *
 * @param <E> the [Engine] to simulate
 */
interface SimulatorConnection<E : Engine<E>> : Connection<E>
