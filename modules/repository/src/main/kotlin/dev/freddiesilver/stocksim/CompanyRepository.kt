package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.company.Company

interface CompanyRepository: Repository<Company> {
    fun createCompany(
        name: String,
        ticker: String,
        description: String,
        volatility: Double,
        drift: Double
    ): Company

    fun findByTicker(ticker: String): Company?
}