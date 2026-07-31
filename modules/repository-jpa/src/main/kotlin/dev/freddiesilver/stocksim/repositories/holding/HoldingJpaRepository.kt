package dev.freddiesilver.stocksim.repositories.holding

import dev.freddiesilver.stocksim.entities.HoldingEntity
import org.springframework.data.jpa.repository.JpaRepository

interface HoldingJpaRepository : JpaRepository<HoldingEntity, Long> {
    fun findByUser_Id(userId: Long): List<HoldingEntity>
    fun findByUser_IdAndStock_Id(userId: Long, stockId: Long): HoldingEntity?
}