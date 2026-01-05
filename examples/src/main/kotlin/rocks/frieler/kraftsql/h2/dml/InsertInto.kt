package rocks.frieler.kraftsql.h2.dml

import rocks.frieler.kraftsql.dml.insertInto
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2ORMapping
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.objects.Table

fun <T : Any> Data<T>.insertInto(table: Table<H2Engine, T>) =
    insertInto(table, H2Engine.DefaultConnection.get())

fun <T : Any> T.insertInto(table: Table<H2Engine, T>) =
    this.insertInto(H2ORMapping, table, H2Engine.DefaultConnection.get())
