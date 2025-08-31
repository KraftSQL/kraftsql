package rocks.frieler.kraftsql.dsl

import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.dml.execute
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine

fun <E : Engine<E>> inTransaction(connection: Connection<E>, content: () -> Unit) {
    BeginTransaction<E>().execute(connection)
    try {
        content.invoke()
        CommitTransaction<E>().execute(connection)
    } catch (exception: Exception) {
        RollbackTransaction<E>().execute(connection)
        throw exception
    }
}
