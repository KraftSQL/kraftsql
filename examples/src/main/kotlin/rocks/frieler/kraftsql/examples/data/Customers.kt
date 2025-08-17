package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.objects.Table

data class Customer(
    val id: Long,
    val country: Country,
)

val customers = Table(name = "customers", type = Customer::class)
