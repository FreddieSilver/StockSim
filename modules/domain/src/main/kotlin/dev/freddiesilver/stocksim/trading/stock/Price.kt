package dev.freddiesilver.stocksim.trading.stock

import java.math.BigDecimal

data class Price(
    val value: BigDecimal
) {
    init {
        require(value >= BigDecimal.ZERO) { "Price cannot be negative" }
    }
}