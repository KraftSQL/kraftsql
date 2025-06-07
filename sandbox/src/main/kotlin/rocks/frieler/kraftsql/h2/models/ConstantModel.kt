package rocks.frieler.kraftsql.h2.models

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemoryConnection
import rocks.frieler.kraftsql.models.ConstantModel

class ConstantModel<T : Any> : ConstantModel<H2Engine, T> {
    constructor(items: Iterable<T>) : super(H2InMemoryConnection.AutoInstance(), items)

    constructor(vararg items: T) : super(H2InMemoryConnection.AutoInstance(), *items)
}
