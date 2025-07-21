package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.objects.Table

data class Product(
    val id: Long,
    val name: String,
    val category: String,
)

val products = Table(name = "products", type = Product::class)
