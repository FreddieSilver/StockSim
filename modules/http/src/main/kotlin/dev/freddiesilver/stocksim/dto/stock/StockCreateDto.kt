package dev.freddiesilver.stocksim.dto.stock

data class StockCreateDto(
    val companyId: Long,
    val initialPrice: Double,
)