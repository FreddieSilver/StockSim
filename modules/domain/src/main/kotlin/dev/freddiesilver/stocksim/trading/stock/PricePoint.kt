package dev.freddiesilver.stocksim.trading.stock

import java.time.Instant

data class PricePoint(
    val id: Long = 0,
    val stockId: Long,
    val price: Price,
    val timestamp: Instant,
)