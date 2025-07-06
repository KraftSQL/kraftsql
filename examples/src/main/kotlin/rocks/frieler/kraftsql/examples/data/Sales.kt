package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.objects.Table
import java.time.Instant

data class Sale(
    val productId: Long,
    val storeId: Long,
    val time: Instant,
    val amount: Int,
) {
    constructor(product: Product, shop: Shop, time: Instant, amount: Int) : this(product.id, shop.id, time, amount)
}

val sales = Table("sales", Sale::class)
