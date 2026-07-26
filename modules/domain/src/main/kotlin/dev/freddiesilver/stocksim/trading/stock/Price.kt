package dev.freddiesilver.stocksim.trading.stock

import java.math.BigDecimal

data class Price(
    val value: BigDecimal,
) {
    init {
        require(value >= BigDecimal.ZERO) { "Price cannot be negative" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Price) return false
        return this.value.compareTo(other.value) == 0
    }

    override fun hashCode(): Int = value.stripTrailingZeros().hashCode()
}
