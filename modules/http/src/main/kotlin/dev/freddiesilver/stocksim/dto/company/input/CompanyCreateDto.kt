package dev.freddiesilver.stocksim.dto.company.input

data class CompanyCreateDto(
    val name: String,
    val ticker: String,
    val description: String,
    val volatility: Double,
    val drift: Double,
)
