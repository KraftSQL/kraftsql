package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.ddl.ColumnDefinition
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.models.Model
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

open class Table<E: Engine<E>, T : Any>(
    connection: Connection<E>,
    val name: String,
    val columns: List<ColumnDefinition<E>>,
) : Model<E, T>(connection) {

    constructor(engine: E, connection: Connection<E>, name: String, type: KClass<T>) : this(
        connection,
        name,
        type.memberProperties.map { field ->
            ColumnDefinition(field.name, engine.getTypeFor(field.returnType))
        }
    )

    override fun sql() = "`$name`"
}
