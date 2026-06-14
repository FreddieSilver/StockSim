package dev.freddiesilver.stocksim.repositories.company

import dev.freddiesilver.stocksim.CompanyRepository
import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.entities.CompanyEntity
import dev.freddiesilver.stocksim.mappers.CompanyMapper

class CompanyRepositoryJpa(
    private val jpa: CompanyJpaRepository
) : CompanyRepository {

    override fun createCompany(
        name: String,
        ticker: String,
        description: String,
        volatility: Double,
        drift: Double
    ): Company {
        val entity = jpa.save(
            CompanyEntity(
                name = name,
                ticker = ticker,
                description = description,
                volatility = volatility,
                baseDrift = drift
            )
        )
        return CompanyMapper.toDomain(entity)
    }

    override fun findByTicker(ticker: String): Company? =
        jpa.findByTicker(ticker)?.let { CompanyMapper.toDomain(it) }

    override fun findById(id: Long): Company? =
        jpa.findById(id).orElse(null)?.let { CompanyMapper.toDomain(it) }

    override fun findAll(): List<Company> =
        jpa.findAll().map { CompanyMapper.toDomain(it) }

    override fun update(entity: Company) {
        jpa.save(CompanyMapper.toEntity(entity))
    }

    override fun deleteById(id: Long) =
        jpa.deleteById(id)

    override fun clear() =
        jpa.deleteAll()
}
