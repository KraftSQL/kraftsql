package rocks.frieler.kraftsql.models

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.ConstantExpression
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties


open class ConstantModel<E : Engine<E>, T : Any> : Model<E, T> {
    val items: Iterable<T>

    constructor(engine: E, items: Iterable<T>) : super(engine) {
        this.items = items
    }

    constructor(engine: E, vararg items: T) : this(engine, items.toList())

    override fun sql(): String {
        return items.joinToString(separator = " UNION ALL ", postfix = ";") { item -> """
            SELECT ${(item::class as KClass<T>).memberProperties.joinToString(", ") { "${
            ConstantExpression<E, Any?>(
                it.get(
                    item
                )
            ).sql()} AS `${it.name}`" }}
        """.trimIndent()}
    }
}
