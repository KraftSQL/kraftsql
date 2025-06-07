package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.ddl.ColumnDefinition
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemoryConnection
import rocks.frieler.kraftsql.objects.Table
import kotlin.reflect.KClass

class Table<T : Any> : Table<H2Engine, T> {
    constructor(name: String, columns: List<ColumnDefinition<H2Engine>>) : super(H2InMemoryConnection.AutoInstance(), name, columns)

    constructor(name: String, type: KClass<T>) : super(H2Engine, H2InMemoryConnection.AutoInstance(), name, type)
}
