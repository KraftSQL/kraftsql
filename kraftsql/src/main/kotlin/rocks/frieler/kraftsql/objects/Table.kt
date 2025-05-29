package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.ddl.ColumnDefinition
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.models.Model
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

open class Table<E: Engine<E>, T : Any>(
    engine: E,
    val name: String,
    val columns: List<ColumnDefinition<E>>,
) : Model<E, T>(engine) {

    constructor(engine: E, name: String, type: KClass<T>) : this(
        engine,
        name,
        type.memberProperties.map { field ->
            ColumnDefinition(
                field.name,
                when (field.returnType) {
                    String::class.starProjectedType -> "CHARACTER VARYING"
                    else -> throw NotImplementedError("Unsupported field type ${field.returnType}")
                }
            )
        }
    )

    override fun sql() = "`$name`"
}
