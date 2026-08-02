package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import java.math.BigDecimal
import java.time.Instant

interface TradeOrderRepository : Repository<TradeOrder> {
    fun createOrder(
        userId: Long,
        stockId: Long,
        type: OrderType,
        quantity: BigDecimal,
        time: Instant
    ): TradeOrder

    fun findByUserId(userId: Long): List<TradeOrder>

    fun findByStockId(stockId: Long): List<TradeOrder>

    fun findByStatus(status: OrderStatus): List<TradeOrder>
}
