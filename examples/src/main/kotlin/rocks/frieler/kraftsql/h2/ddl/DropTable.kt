package rocks.frieler.kraftsql.h2.ddl

import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemoryConnection
import rocks.frieler.kraftsql.objects.Table

fun Table<H2Engine, *>.drop(ifExists: Boolean = false) {
    H2InMemoryConnection.Default.get().execute(DropTable(this, ifExists))
}
