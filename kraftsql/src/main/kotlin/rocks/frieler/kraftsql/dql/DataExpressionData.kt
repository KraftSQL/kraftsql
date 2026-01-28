package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.HasColumns

/**
 * [Data] implementation as the representation of data returned by an [Expression].
 *
 * @param E the [Engine] targeted
 * @param T the Kotlin type of the [Data]'s rows
 * @param expression the [Expression] that returns [Data]
 */
class DataExpressionData<E : Engine<E>, T : Any>(val expression: Expression<E, Data<E, T>>) : Data<E, T> {

    override val columnNames
        get() =
            if (HasColumns::class.isInstance(expression)) {
                (expression as HasColumns<*, *>).columnNames
            } else {
                listOf(expression.defaultColumnName())
            }

    override fun sql() = expression.sql()

    override fun get(column: String): Column<E, Any?> {
        // TODO: check if column exists (as in HasColumns), once columns are definitely known
        return Column(column)
    }

    override fun equals(other: Any?) = other is DataExpressionData<*, *>
            && expression == other.expression

    override fun hashCode(): Int = expression.hashCode()
}
