package rocks.frieler.kraftsql.h2.dml

import rocks.frieler.kraftsql.dml.insertInto
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemoryConnection
import rocks.frieler.kraftsql.models.Model
import rocks.frieler.kraftsql.objects.Table

fun <T : Any> Model<H2Engine, T>.insertInto(table: Table<H2Engine, T>) = insertInto(table, H2InMemoryConnection.AutoInstance())
