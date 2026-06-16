package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.holding.Holding

interface HoldingRepository : Repository<Holding> {
    fun createHolding(
        userId: Long,
        stockId: Long,
        quantity: Int,
    ): Holding

    fun findByUserAndStock(
        userId: Long,
        stockId: Long,
    ): Holding?

    fun findByUserId(userId: Long): List<Holding>
}
