package dev.freddiesilver.stocksim.dto.tradeorder

data class TradeOrderRequestDto(
    val stockId:Long,
    val type: String, // BUY or SELL
    val quantity: Double,
)