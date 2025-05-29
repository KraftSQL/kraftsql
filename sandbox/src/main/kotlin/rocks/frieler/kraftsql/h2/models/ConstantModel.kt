package rocks.frieler.kraftsql.h2.models

import rocks.frieler.kraftsql.h2.engine.H2InMemoryEngine
import rocks.frieler.kraftsql.models.ConstantModel

class ConstantModel<T : Any> : ConstantModel<H2InMemoryEngine, T> {
    constructor(items: Iterable<T>) : super(H2InMemoryEngine.AutoInstance(), items)

    constructor(vararg items: T) : super(H2InMemoryEngine.AutoInstance(), *items)
}
