package dev.freddiesilver.stocksim.repositories.user

import dev.freddiesilver.stocksim.entities.TokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TokenJpaRepository : JpaRepository<TokenEntity, Long> {
    fun findByTokenValidationInfo(tokenValidationInfo: String): TokenEntity?

    fun findAllByUserIdOrderByLastUsedAtAsc(userId: Long): List<TokenEntity>

    fun deleteByTokenValidationInfo(tokenValidationInfo: String): Long

    fun countByUserId(userId: Long): Long
}
