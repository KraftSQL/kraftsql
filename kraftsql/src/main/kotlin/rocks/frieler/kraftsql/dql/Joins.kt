package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression

/**
 * Abstract base class for SQL Joins.
 *
 * @param <E> the [Engine] to execute this [Join]
 * @param data the data to join
 * @param condition the condition to join on
 */
abstract class Join<E : Engine<E>>(
    val data: QuerySource<E, *>,
    val condition: Expression<E, Boolean>,
) {
    abstract fun sql(): String
}

/**
 * SQL INNER JOIN that keeps only rows with matching parts from both sides.
 *
 * @param <E> the [Engine] to execute this [Join]
 * @param data the data to join
 * @param condition the condition to join on
 */
class InnerJoin<E : Engine<E>>(
    data: QuerySource<E, *>,
    condition: Expression<E, Boolean>,
) : Join<E>(data, condition) {
    override fun sql() = "INNER JOIN ${data.sql()} ON ${condition.sql()}"
}

/**
 * SQL LEFT JOIN that keeps all rows from the left side, either once per matching row from the right side, or once with
 * the columns from the right side set to NULL, if there is no matching row.
 *
 * @param <E> the [Engine] to execute this [Join]
 * @param data the data to join
 * @param condition the condition to join on
 */
class LeftJoin<E : Engine<E>>(
    data: QuerySource<E, *>,
    condition: Expression<E, Boolean>,
) : Join<E>(data, condition) {
    override fun sql() = "LEFT JOIN ${data.sql()} ON ${condition.sql()}"
}

/**
 * SQL RIGHT JOIN that keeps all rows from the right side, either once per matching row from the left side, or once with
 * the columns from the left side set to NULL, if there is no matching row.
 *
 * @param <E> the [Engine] to execute this [Join]
 * @param data the data to join
 * @param condition the condition to join on
 */
class RightJoin<E : Engine<E>>(
    data: QuerySource<E, *>,
    condition: Expression<E, Boolean>,
) : Join<E>(data, condition) {
    override fun sql() = "RIGHT JOIN ${data.sql()} ON ${condition.sql()}"
}
