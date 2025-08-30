package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.ddl.drop
import rocks.frieler.kraftsql.h2.dml.execute
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.objects.Table
import rocks.frieler.kraftsql.objects.DataRow

fun main() {
    try {
        products.create()
        Product(1, "Apple", Category(1, "Food")).insertInto(products)
        Product(2, "Pants", Category(2, "Clothes")).insertInto(products)

        deleteFood(products).also { println("Deleted $it products.") }

        Select<DataRow> { from(products) }
            .execute()
            .forEach { println(it) }

    } finally {
        products.drop(ifExists = true)
    }
}

fun deleteFood(productTable: Table<Product>) =
    Delete(productTable, productTable[Product::category][Category::name] `=` Constant("Food"))
        .execute()
