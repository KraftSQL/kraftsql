package rocks.frieler.kraftsql.h2.dml

import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.execute
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemoryConnection

fun Delete<H2Engine>.execute() = execute(H2InMemoryConnection.Default.get())
