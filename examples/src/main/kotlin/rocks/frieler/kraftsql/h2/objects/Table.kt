package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2ORMapping
import rocks.frieler.kraftsql.objects.Table
import kotlin.reflect.KClass

class Table<T : Any> : Table<H2Engine, T> {
    constructor(database: String? = null, schema: String? = null, name: String, columns: List<Column<H2Engine>>) : super(database, schema, name, columns)

    constructor(database: String? = null, schema: String? = null, name: String, type: KClass<T>) : super(H2ORMapping, database, schema, name, type)
}
