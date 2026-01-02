package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2ORMapping
import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.objects.ConstantData

class ConstantData<T : Any> : ConstantData<H2Engine, T> {
    constructor(items: Iterable<T>) : super(H2ORMapping, items)

    constructor(vararg items: T) : super(H2ORMapping, *items)

    private constructor(columnNames: List<String>) : super(H2ORMapping, emptyList(), columnNames)

    companion object {
        fun <T : Any> empty(columnNames: List<String>) = ConstantData<T>(columnNames)
        inline fun <reified T : Any> empty() = empty<T>(H2ORMapping.getSchemaFor(T::class).map { it.name })
    }

    override fun get(field: String) = Column<Any?>(field)
}
