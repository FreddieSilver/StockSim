package dev.freddiesilver.stocksim.trading.tradeorder

import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.user.User
import java.math.BigDecimal

data class TradeOrder(
    val id: Long = 0,
    val user: User,
    val stock: Stock,
    val type: OrderType,
    val quantity: Int,
    val priceValueAtOrder: BigDecimal,
    var status: OrderStatus
)