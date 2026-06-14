package dev.freddiesilver.stocksim.repositories.stock

import dev.freddiesilver.stocksim.entities.StockEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StockJpaRepository : JpaRepository<StockEntity, Long>
