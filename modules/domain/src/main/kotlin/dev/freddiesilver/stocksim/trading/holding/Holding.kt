package dev.freddiesilver.stocksim.trading.holding

data class Holding(
    val id: Long = 0,
    val userId: Long,
    val stockId: Long,
    var quantity: Int
){
    init {
        require(quantity >= 0) { "Portfolio quantity cannot be negative" }
    }

    fun addQuantity(amount: Int) {
        require(amount > 0) { "Amount to add must be positive" }
        this.quantity += amount
    }

    fun removeQuantity(amount: Int) {
        require(amount > 0) { "Amount to remove must be positive" }
        require(this.quantity >= amount) { "Cannot remove more than owned" }
        this.quantity -= amount
    }
}