package dev.freddiesilver.stocksim.company

data class Company(
    val id: Long,
    val name: CompanyName,
    val ticker: Ticker,
    val description: Description,
    val volatility: Double,
    val drift: Double
)