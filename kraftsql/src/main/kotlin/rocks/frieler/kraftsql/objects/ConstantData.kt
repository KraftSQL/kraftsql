package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Constant
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

open class ConstantData<E : Engine<E>, T : Any> : Data<E, T> {
    val items: Iterable<T>

    constructor(items: Iterable<T>) : super() {
        this.items = items
    }

    constructor(vararg items: T) : this( items.toList())

    override fun sql(): String {
        return items.joinToString(separator = " UNION ALL ") { item -> """
            SELECT ${(item::class as KClass<T>).memberProperties.joinToString(", ") { "${
            Constant<E, Any?>(
                it.get(
                    item
                )
            ).sql()} AS `${it.name}`" }}
        """.trimIndent()}
    }
}
