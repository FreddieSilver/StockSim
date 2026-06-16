package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.user.User

interface TradeOrderRepository : Repository<TradeOrder> {
    fun createOrder(
        user: User,
        stock: Stock,
        type: OrderType,
        quantity: Int,
    ): TradeOrder

    fun findByUserId(userId: Long): List<TradeOrder>

    fun findByStockId(stockId: Long): List<TradeOrder>

    fun findByStatus(status: OrderStatus): List<TradeOrder>
}
