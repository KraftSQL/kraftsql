package rocks.frieler.kraftsql.dsl

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNotNull

/**
 * Build an [IsNotNull]-expression from this [Expression].
 *
 * @param E the SQL [Engine]
 * @return an [IsNotNull]-expression that checks, whether the given expression is not `NULL`.
 */
fun <E : Engine<E>> Expression<E, *>.isNotNull() = IsNotNull(this)
