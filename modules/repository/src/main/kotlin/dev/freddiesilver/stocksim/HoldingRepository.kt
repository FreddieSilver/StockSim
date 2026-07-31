package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.holding.Holding
import java.math.BigDecimal

interface HoldingRepository : Repository<Holding> {
    fun createHolding(
        userId: Long,
        stockId: Long,
        quantity: BigDecimal,
    ): Holding

    fun findByUserIdAndStockId(
        userId: Long,
        stockId: Long,
    ): Holding?

    fun findByUserId(userId: Long): List<Holding>
}