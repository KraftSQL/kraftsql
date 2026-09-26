package rocks.frieler.kraftsql.local.engine

import rocks.frieler.kraftsql.data.ConstantData
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.DataRow
import rocks.frieler.kraftsql.engine.DataClassSerDe
import rocks.frieler.kraftsql.engine.Engine
import kotlin.reflect.KClass

/**
 * [Engine] implementation that works locally in the JVM.
 *
 * The [KraftSqlLocalEngine] is primarily meant as a dummy [Engine] for (unit) testing business logic, when the real
 * [Engine] is not available or too expensive to use. It is not optimized and not intended to handle "Big Data".
 */
class KraftSqlLocalEngine : Engine {
    override fun <T : Any> collect(data: Data<T>, type: KClass<T>): List<T> {
        return when {
            data is ConstantData<T> -> data.items
                .map { if (it !is DataRow) DataClassSerDe.serialize(it) else it }
                .map { @Suppress("UNCHECKED_CAST") if (type != DataRow::class) DataClassSerDe.deserialize(it, type) else it as T }
            else -> throw NotImplementedError("Collecting $data is not implemented.")
        }
    }
}
