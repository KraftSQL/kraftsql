package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.objects.ConstantData

class ConstantData<T : Any> : ConstantData<H2Engine, T> {
    constructor(items: Iterable<T>) : super(items)

    constructor(vararg items: T) : super(*items)
}
