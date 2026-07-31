package dev.freddiesilver.stocksim.trading.holding

import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.user.User
import java.math.BigDecimal

data class Holding(
    val id: Long = 0,
    val user: User,
    val stock: Stock,
    var quantity: BigDecimal,
) {
    init {
        require(quantity >= BigDecimal.ZERO) { "Portfolio quantity cannot be negative" }
    }

    fun addQuantity(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Amount to add must be positive" }
        this.quantity += amount
    }

    fun removeQuantity(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Amount to remove must be positive" }
        require(this.quantity >= amount) { "Cannot remove more than owned" }
        this.quantity -= amount
    }
}
