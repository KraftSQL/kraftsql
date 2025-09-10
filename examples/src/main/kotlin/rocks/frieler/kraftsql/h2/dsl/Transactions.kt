package rocks.frieler.kraftsql.h2.dsl

import rocks.frieler.kraftsql.dsl.inTransaction
import rocks.frieler.kraftsql.h2.engine.H2Engine

fun inTransaction(content: () -> Unit) {
    inTransaction(H2Engine.DefaultConnection.get(), content)
}
