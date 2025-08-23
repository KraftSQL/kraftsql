package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.objects.Table
import java.math.BigDecimal
import java.time.Instant

data class Purchase(
    val id: Long,
    val customerId: Long,
    val orderTime: Instant,
    val totalPrice: BigDecimal,
) {
    constructor(id: Long, customer: Customer, orderTime: Instant, totalPrice: BigDecimal) :
            this(id, customer.id, orderTime, totalPrice)
}

val purchases = Table(name = "purchases", type = Purchase::class)

data class PurchaseItem(
    val purchaseId: Long,
    val product: ProductOutline,
    val pricePerUnit: BigDecimal,
    val amount: Int,
) {
    constructor(purchase: Purchase, product: Product, pricePerUnit: BigDecimal, amount: Int) :
            this(purchase.id, ProductOutline(product), pricePerUnit, amount)
}

val purchaseItems = Table(name = "purchase_items", type = PurchaseItem::class)

data class ProductOutline(
    val id: Long,
    val name: String,
) {
    constructor(product: Product) : this(product.id, product.name)
}
