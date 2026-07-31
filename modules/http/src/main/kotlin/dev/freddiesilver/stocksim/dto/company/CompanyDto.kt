package dev.freddiesilver.stocksim.dto.company

data class CompanyDto(
    val id: Long,
    val name: String,
    val ticker: String,
    val description: String,
    val volatility: Double,
    val drift: Double,
)