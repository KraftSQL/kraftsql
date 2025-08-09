package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.ORMapping

open class ConstantData<E : Engine<E>, T : Any> : Data<E, T> {
    val orm: ORMapping<E, *>
    val items: Iterable<T>

    constructor(orm: ORMapping<E, *>, items: Iterable<T>) : super() {
        this.orm = orm
        this.items = items
    }

    constructor(orm: ORMapping<E, *>, vararg items: T) : this(orm, items.toList())

    override fun sql(): String {
        return items.joinToString(separator = " UNION ALL ") { item ->
            "(SELECT ${orm.serialize(item).entries.joinToString(", ") { "${it.value.sql()} AS `${it.key}`" }})"
        }
    }
}
