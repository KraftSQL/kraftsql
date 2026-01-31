package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression

/**
 * Abstract base class for SQL Joins.
 *
 * @param E the [Engine] to execute this [Join]
 * @param data the data to join
 * @param condition the condition to join on
 */
abstract class Join<E : Engine<E>>(
    val data: QuerySource<E, *>,
    open val condition: Expression<E, Boolean?>? = null,
) {
    abstract fun sql(): String
}

/**
 * SQL INNER JOIN that keeps only rows with matching parts from both sides.
 *
 * @param E the [Engine] to execute this [Join]
 * @param data the data to join
 * @param condition the condition to join on
 */
class InnerJoin<E : Engine<E>>(
    data: QuerySource<E, *>,
    override val condition: Expression<E, Boolean?>,
) : Join<E>(data, condition) {
    override fun sql() = "INNER JOIN ${data.sql()} ON ${condition.sql()}"
}

/**
 * SQL LEFT JOIN that keeps all rows from the left side, either once per matching row from the right side, or once with
 * the columns from the right side set to NULL, if there is no matching row.
 *
 * @param E the [Engine] to execute this [Join]
 * @param data the data to join
 * @param condition the condition to join on
 */
class LeftJoin<E : Engine<E>>(
    data: QuerySource<E, *>,
    override val condition: Expression<E, Boolean?>,
) : Join<E>(data, condition) {
    override fun sql() = "LEFT JOIN ${data.sql()} ON ${condition.sql()}"
}

/**
 * SQL RIGHT JOIN that keeps all rows from the right side, either once per matching row from the left side, or once with
 * the columns from the left side set to NULL, if there is no matching row.
 *
 * @param E the [Engine] to execute this [Join]
 * @param data the data to join
 * @param condition the condition to join on
 */
class RightJoin<E : Engine<E>>(
    data: QuerySource<E, *>,
    override val condition: Expression<E, Boolean?>,
) : Join<E>(data, condition) {
    override fun sql() = "RIGHT JOIN ${data.sql()} ON ${condition.sql()}"
}

/**
 * SQL CROSS JOIN that results in the cartesian product of both sides.
 *
 * @param E the [Engine] to execute this [Join]
 * @param data the data to join
 */
class CrossJoin<E : Engine<E>>(
    data: QuerySource<E, *>,
) : Join<E>(data) {
    override fun sql() = "CROSS JOIN ${data.sql()}"
}
