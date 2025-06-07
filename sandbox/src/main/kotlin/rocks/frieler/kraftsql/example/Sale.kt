package rocks.frieler.kraftsql.example

import java.time.Instant

data class Sale(
    val productId: Long,
    val storeId: Long,
    val time: Instant,
    val amount: Int,
) {
    constructor(product: Product, store: Store, time: Instant, amount: Int) : this(product.id, store.id, time, amount)
}
