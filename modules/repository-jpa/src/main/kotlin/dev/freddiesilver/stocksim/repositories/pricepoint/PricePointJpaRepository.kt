package dev.freddiesilver.stocksim.repositories.pricepoint

import dev.freddiesilver.stocksim.entities.PricePointEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PricePointJpaRepository: JpaRepository<PricePointEntity, Long> {
    // Pageable to get the most recent points efficiently
    fun findByStockIdOrderByTimestampDesc(stockId: Long, pageable: Pageable): List<PricePointEntity>
}