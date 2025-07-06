package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.objects.Table
import kotlin.reflect.KClass

class Table<T : Any> : Table<H2Engine, T> {
    constructor(name: String, columns: List<Column<H2Engine>>) : super(name, columns)

    constructor(name: String, type: KClass<T>) : super(H2Engine, name, type)
}
