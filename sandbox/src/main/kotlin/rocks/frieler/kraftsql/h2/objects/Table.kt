package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.ddl.ColumnDefinition
import rocks.frieler.kraftsql.h2.engine.H2InMemoryEngine
import rocks.frieler.kraftsql.objects.Table
import kotlin.reflect.KClass

class Table<T : Any> : Table<H2InMemoryEngine, T> {
    constructor(name: String, columns: List<ColumnDefinition<H2InMemoryEngine>>) : super(H2InMemoryEngine.AutoInstance(), name, columns)

    constructor(name: String, type: KClass<T>) : super(H2InMemoryEngine.AutoInstance(), name, type)
}
