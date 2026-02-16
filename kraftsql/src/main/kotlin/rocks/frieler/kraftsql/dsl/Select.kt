package rocks.frieler.kraftsql.dsl

import rocks.frieler.kraftsql.dql.CrossJoin
import rocks.frieler.kraftsql.dql.DataExpressionData
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.HasColumns
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.Join
import rocks.frieler.kraftsql.dql.LeftJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.RightJoin
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.expressions.knownNotNull
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

/**
 * Creates a new [Select] statement defined via the DSL.
 *
 * Example:
 * ```kotlin
 * val select = SELECT<DataRow> {
 *     from(data)
 *     column(data["id"] `as` "match")
 *     where(data["type"] `=` Constant("A"))
 * }
 * ```
 * would render to ``SELECT `id` AS `match` FROM data WHERE `type` = 'A'``.
 *
 * See [SelectBuilder] for all available options to define the SELECT statement.
 *
 * @param E the [Engine] where the resulting [Select] statement can be executed
 * @param T the Kotlin type of the rows returned by the resulting [Select] statement
 * @param configurator a function on a [SelectBuilder] to configure the [Select] statement
 * @return the [Select] statement
 */
fun <E : Engine<E>, T : Any> Select(configurator: @SqlDsl SelectBuilder<E, T>.() -> Unit) : Select<E, T> {
    return SelectBuilder<E, T>().apply { configurator() }.build()
}

@SqlDsl
open class SelectBuilder<E : Engine<E>, T : Any> {
    private lateinit var source: QuerySource<E, *>
    private val joins: MutableList<Join<E>> = mutableListOf()
    private val columns: MutableList<Projection<E, *>> = mutableListOf()
    private lateinit var filter: Expression<E, Boolean?>
    private val grouping: MutableList<Expression<E, *>> = mutableListOf()

    open fun <S: Any> from(source: QuerySource<E, S>) : HasColumns<E, S> {
        check(!this::source.isInitialized) { "SELECT already has a source to select from." }
        return source
            .also { this.source = it }
    }

    open fun <S: Any> from(source: Data<E, S>) = from(QuerySource(source))

    fun column(column: Projection<E, *>) {
        columns.add(column)
    }

    fun column(column: Expression<E, *>) {
        column(Projection(column))
    }

    fun columns(vararg columns: Projection<E, *>) {
        this.columns.addAll(columns)
    }

    /**
     * [INNER JOIN][InnerJoin]s the given data on the given condition.
     *
     * @param data the data to join
     * @param condition the condition to join on
     * @return the data to join for further usage
     */
    open fun <J : Any> innerJoin(data: QuerySource<E, J>, condition: @SqlDsl QuerySource<E, J>.() -> Expression<E, Boolean?>) : HasColumns<E, J> {
        return data
            .also { joins.add(InnerJoin(it, condition(data))) }
    }

    /**
     * [INNER JOIN][InnerJoin]s the given data on the given condition.
     *
     * @param data the data to join
     * @param condition the condition to join on
     * @return the data to join for further usage
     */
    open fun <J : Any> innerJoin(data: Data<E, J>, condition: @SqlDsl QuerySource<E, J>.() -> Expression<E, Boolean?>) =
        innerJoin(QuerySource(data), condition)

    /**
     * [LEFT JOIN][LeftJoin]s the given data on the given condition.
     *
     * @param data the data to join
     * @param condition the condition to join on
     * @return the data to join for further usage
     */
    open fun <J : Any> leftJoin(data: QuerySource<E, J>, condition: @SqlDsl QuerySource<E, J>.() -> Expression<E, Boolean?>) : HasColumns<E, J> {
        return data
            .also { joins.add(LeftJoin(it, condition(data))) }
    }

    /**
     * [LEFT JOIN][LeftJoin]s the given data on the given condition.
     *
     * @param data the data to join
     * @param condition the condition to join on
     * @return the data to join for further usage
     */
    open fun <J : Any> leftJoin(data: Data<E, J>, condition: @SqlDsl QuerySource<E, J>.() -> Expression<E, Boolean?>) =
        leftJoin(QuerySource(data), condition)

    /**
     * [RIGHT JOIN][RightJoin]s the given data on the given condition.
     *
     * @param data the data to join
     * @param condition the condition to join on
     * @return the data to join for further usage
     */
    open fun <J : Any> rightJoin(data: QuerySource<E, J>, condition: @SqlDsl QuerySource<E, J>.() -> Expression<E, Boolean?>) : HasColumns<E, J> {
        return data
            .also { joins.add(RightJoin(it, condition(data))) }
    }

    /**
     * [RIGHT JOIN][RightJoin]s the given data on the given condition.
     *
     * @param data the data to join
     * @param condition the condition to join on
     * @return the data to join for further usage
     */
    open fun <J : Any> rightJoin(data: Data<E, J>, condition: @SqlDsl QuerySource<E, J>.() -> Expression<E, Boolean?>) =
        rightJoin(QuerySource(data), condition)

    /**
     * [CROSS JOIN][CrossJoin]s the given data.
     *
     * @param data the data to join
     * @return the data to join for further usage
     */
    open fun <J : Any> crossJoin(data: QuerySource<E, J>) : HasColumns<E, J> =
        data.also { joins.add(CrossJoin(it)) }

    /**
     * [CROSS JOIN][CrossJoin]s the given data.
     *
     * @param data the data to join
     * @return the data to join for further usage
     */
    open fun <J : Any> crossJoin(data: Data<E, J>) = crossJoin(QuerySource(data))

    fun where(filter: Expression<E, Boolean?>) {
        check(!::filter.isInitialized) { "SELECT already has a WHERE-filter." }
        this.filter = filter
    }

    fun groupBy(vararg columns: Expression<E, *>) {
        grouping.addAll(columns)
    }

    open fun build(): Select<E, T> = Select(
        source,
        joins,
        columns.takeIf { it.isNotEmpty() },
        if (::filter.isInitialized) filter else null,
        grouping,
    )
}

/**
 * Assigns an alias to the specified [Data] instance wrapping it into a [QuerySource].
 *
 * @param E the [Engine] where this [Data] resides and can be worked with
 * @param T the Kotlin type of the [Data]'s rows
 * @param alias the alias to assign to the [Data]
 * @return a [QuerySource] wrapping the [Data] with the specified alias
 */
infix fun <E : Engine<E>, T : Any> Data<E, T>.`as`(alias: String) = QuerySource(this, alias)

/**
 * Assigns an alias to the specified [Data]-valued [Expression] instance wrapping it into a [QuerySource].
 *
 * @param E the [Engine] where this [Expression] is executed
 * @param T the Kotlin type of the rows of the [Expression]'s result
 * @param alias the alias to assign to the [Expression]
 * @return a [QuerySource] wrapping the [Expression] with the specified alias
 */
infix fun <E : Engine<E>, T : Any> Expression<E, Data<E, T>>.`as`(alias: String) = DataExpressionData(this) `as` alias

infix fun <E : Engine<E>, T> Expression<E, T>.`as`(alias: String) = Projection(this, alias)

/**
 * Creates a [Projection] from an [Expression] using a [KProperty] of a data-class describing the result schema.
 *
 * Using a [KProperty] does not only use its name but also checks the type, including nullability. Be aware that `T` is
 * often automatically inferred to be nullable by Kotlin as a lor of [Expression]s are nullable. You can use
 * [knownNotNull] on the [Expression] to prevent this.
 *
 * @param E the [Engine] where the [Expression] and [Projection] happen
 * @param T the Kotlin type of the result column
 * @param field the [KProperty] describing the result column
 * @return a [Projection] for the given [Expression] named after and checked against the [KProperty]
 * @throws IllegalArgumentException if the [KProperty] is not nullable, but the [Expression] is
 */
inline infix fun <E: Engine<E>, reified T> Expression<E, T>.`as`(field: KProperty<T>): Projection<E, T> {
    require(!typeOf<T>().isMarkedNullable || field.returnType.isMarkedNullable) {
        "field '$field' is declared as non-nullable, but the expression '${sql()}' is nullable."
    }
    return Projection(this, field.name)
}
