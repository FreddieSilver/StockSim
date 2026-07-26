package dev.freddiesilver.stocksim.dto

data class StockCreateDto(
    val companyId: Long,
    val initialPrice: Double,
)
