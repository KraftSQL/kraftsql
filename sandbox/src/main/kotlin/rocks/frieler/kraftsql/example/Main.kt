package rocks.frieler.kraftsql.example

import rocks.frieler.kraftsql.ddl.create
import rocks.frieler.kraftsql.dml.insertInto
import rocks.frieler.kraftsql.h2.models.ConstantModel
import rocks.frieler.kraftsql.h2.queries.Select
import rocks.frieler.kraftsql.expressions.ColumnExpression
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.h2.objects.Table
import rocks.frieler.kraftsql.queries.execute

fun main() {
    val entities = Table("entity", Entity::class)
        .apply { create() }

    ConstantModel(
        Entity("foo", "bar"),
        Entity("K", "Kotlin"),
    )
        .apply { insertInto(entities) }

    val count = Select.invoke(
        from = entities,
        columns = listOf(ColumnExpression(Count(), "_count")),
    ).execute().single()["_count"]
    println(count)
}
