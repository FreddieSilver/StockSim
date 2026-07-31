package dev.freddiesilver.stocksim.dto.tradeorder

import dev.freddiesilver.stocksim.dto.stock.StockDto

data class TradeOrderDto(
    val stock: StockDto,
    val type: String,
    val quantity: Double,
    val priceValueAtOrder: Double,
    val status: String,
)