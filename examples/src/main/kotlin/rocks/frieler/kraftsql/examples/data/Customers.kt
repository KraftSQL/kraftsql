package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.objects.Table
import java.time.LocalDate

data class Customer(
    val id: Long,
    val country: Country,
    val dateOfBirth: LocalDate,
)

val customers = Table(name = "customers", type = Customer::class)
