package rocks.frieler.kraftsql.h2.ddl

import rocks.frieler.kraftsql.ddl.create
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.objects.Table

fun <T : Any> Table<H2Engine, T>.create() {
    create(H2Engine.DefaultConnection.get())
}
