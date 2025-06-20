package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Equals<E : Engine<E>>(
    private val left: Expression<E, *>,
    private val right: Expression<E, *>,
) : Expression<E, Boolean> {
    override fun sql() = "(${left.sql()})=(${right.sql()})"
}

infix fun <E : Engine<E>> Expression<E, *>.`=`(other: Expression<E, *>): Equals<E> = Equals(this, other)
