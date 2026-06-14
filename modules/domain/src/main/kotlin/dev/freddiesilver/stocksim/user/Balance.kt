package dev.freddiesilver.stocksim.user

import java.math.BigDecimal

data class Balance(
    val value: BigDecimal
) {
    init {
        require(value >= BigDecimal.ZERO) { "Balance cannot be negative" }
    }
}