package dev.freddiesilver.stocksim.repositories.company

import dev.freddiesilver.stocksim.entities.CompanyEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyJpaRepository : JpaRepository<CompanyEntity, Long> {
    fun findByTicker(ticker: String): CompanyEntity?
}
