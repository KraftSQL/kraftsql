package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

open class Table<E: Engine<E>, T : Any>(
    val name: String,
    val columns: List<Column<E>>,
) : Data<E, T> {

    constructor(engine: E, name: String, type: KClass<T>) : this(
        name,
        type.memberProperties.map { field ->
            Column(field.name, engine.getTypeFor(field.returnType))
        }
    )

    override fun <V> get(field: String): rocks.frieler.kraftsql.expressions.Column<E, V> {
        check(columns.any { it.name == field }) { "no column '${field}' in table '$name'" }
        return super.get(field)
    }

    override fun sql() = "`$name`"
}
