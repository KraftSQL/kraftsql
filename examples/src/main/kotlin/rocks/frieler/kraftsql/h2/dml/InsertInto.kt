package rocks.frieler.kraftsql.h2.dml

import rocks.frieler.kraftsql.dml.insertInto
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemorySession
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.Table

fun <T : Any> Data<H2Engine, T>.insertInto(table: Table<H2Engine, T>) =
    insertInto(table, H2InMemorySession.Default.get())

fun <T : Any> T.insertInto(table: Table<H2Engine, T>) =
    this.insertInto(table, H2InMemorySession.Default.get())
