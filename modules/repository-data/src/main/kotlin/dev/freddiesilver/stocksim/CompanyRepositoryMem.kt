package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker

class CompanyRepositoryMem: CompanyRepository {

    private val companies = mutableListOf<Company>()
    private var nextId = 1L

    override fun createCompany(
        name: String,
        ticker: String,
        description: String,
        volatility: Double,
        drift: Double
    ): Company {
        val company = Company(
            id = nextId++,
            name = CompanyName(name),
            ticker = Ticker(ticker),
            description = Description(description),
            volatility = volatility,
            drift = drift
        )
        companies.add(company)
        return company
    }

    override fun findByTicker(ticker: String): Company? =
        companies.find { it.ticker.value == ticker }

    override fun findById(id: Long): Company? =
        companies.find { it.id == id }

    override fun findAll(): List<Company> =
        companies.toList()

    override fun update(entity: Company) {
        val index = companies.indexOfFirst { it.id == entity.id }
        if (index != -1) {
            companies[index] = entity
        } else {
            throw IllegalArgumentException("Company with id ${entity.id} not found")
        }
    }

    override fun deleteById(id: Long) {
        val index = companies.indexOfFirst { it.id == id }
        if (index != -1) {
            companies.removeAt(index)
        } else {
            throw IllegalArgumentException("Company with id $id not found")
        }
    }

    override fun clear() =
        companies.clear()
}