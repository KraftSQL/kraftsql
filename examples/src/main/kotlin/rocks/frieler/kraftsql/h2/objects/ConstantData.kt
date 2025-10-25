package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2ORMapping
import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.objects.ConstantData

class ConstantData<T : Any> : ConstantData<H2Engine, T> {
    constructor(items: Iterable<T>) : super(H2ORMapping, items)

    constructor(vararg items: T) : super(H2ORMapping, *items)

    override fun get(field: String) = Column<Any?>(field)
}
