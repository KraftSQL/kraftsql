package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.models.Model
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

open class Table<E: Engine<E>, T : Any>(
    val name: String,
    val columns: List<Column<E>>,
) : Model<E, T>() {

    constructor(engine: E, name: String, type: KClass<T>) : this(
        name,
        type.memberProperties.map { field ->
            Column(field.name, engine.getTypeFor(field.returnType))
        }
    )

    override operator fun <V> get(property: KProperty1<T, V>) : rocks.frieler.kraftsql.expressions.Column<E, V> {
        check(columns.any { it.name == property.name }) { "no column '${property.name}' in table '$name'" }
        return super[property]
    }

    override fun sql() = "`$name`"
}
