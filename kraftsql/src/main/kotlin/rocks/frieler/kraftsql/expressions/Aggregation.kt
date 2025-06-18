package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

interface Aggregation<E: Engine<E>, T> : Expression<E, T>
