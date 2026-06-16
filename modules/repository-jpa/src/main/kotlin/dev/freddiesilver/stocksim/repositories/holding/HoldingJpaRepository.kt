package dev.freddiesilver.stocksim.repositories.holding

import dev.freddiesilver.stocksim.entities.HoldingEntity
import org.springframework.data.jpa.repository.JpaRepository

interface HoldingJpaRepository : JpaRepository<HoldingEntity, Long> {
    fun findByUserId(userId: Long): List<HoldingEntity>

    fun findByUserIdAndStockId(
        userId: Long,
        stockId: Long,
    ): HoldingEntity?

    fun deleteByUserIdAndStockId(
        userId: Long,
        stockId: Long,
    )
}
