package dev.freddiesilver.stocksim.repositories.user

import dev.freddiesilver.stocksim.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?
}