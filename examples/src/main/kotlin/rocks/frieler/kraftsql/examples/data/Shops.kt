package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.objects.Table

data class Shop(
    val id: Long,
    val country: String,
)

val shops = Table(name = "shops", type = Shop::class)
