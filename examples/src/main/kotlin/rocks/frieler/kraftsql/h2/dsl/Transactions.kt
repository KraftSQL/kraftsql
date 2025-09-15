package rocks.frieler.kraftsql.h2.dsl

import rocks.frieler.kraftsql.dsl.transaction
import rocks.frieler.kraftsql.h2.engine.H2Engine

fun transaction(content: () -> Unit) {
    transaction(H2Engine.DefaultConnection.get(), content)
}
