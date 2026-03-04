package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.objects.Table
import java.math.BigDecimal
import java.time.Instant

data class Purchase(
    val id: Long,
    val customerId: Long,
    val orderTime: Instant,
    val items: Array<PurchaseItem>,
    val totalPrice: BigDecimal,
) {
    constructor(id: Long, customer: Customer, orderTime: Instant, items: Array<PurchaseItem>, totalPrice: BigDecimal) :
            this(id, customer.id, orderTime, items, totalPrice)

    override fun equals(other: Any?) = other is Purchase
            && id == other.id
            && customerId == other.customerId
            && orderTime == other.orderTime
            && items.contentEquals(other.items)
            && totalPrice == other.totalPrice

    override fun hashCode(): Int = id.hashCode()
}

val purchases = Table(name = "purchases", type = Purchase::class)

data class PurchaseItem(
    val product: ProductOutline,
    val pricePerUnit: BigDecimal,
    val amount: Int,
) {
    constructor(product: Product, pricePerUnit: BigDecimal, amount: Int) :
            this(ProductOutline(product), pricePerUnit, amount)
}

data class ProductOutline(
    val id: Long,
    val name: String,
) {
    constructor(product: Product) : this(product.id, product.name)
}
