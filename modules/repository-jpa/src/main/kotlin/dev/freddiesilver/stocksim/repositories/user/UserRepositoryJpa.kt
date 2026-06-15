package dev.freddiesilver.stocksim.repositories.user

import dev.freddiesilver.stocksim.UserRepository
import dev.freddiesilver.stocksim.entities.UserEntity
import dev.freddiesilver.stocksim.mappers.UserMapper
import dev.freddiesilver.stocksim.user.Balance
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import java.math.BigDecimal

class UserRepositoryJpa(
    private val jpa: UserJpaRepository
) : UserRepository {

    override fun createUser(username: String, balance: BigDecimal): User {
        val entity = UserEntity(
            username = username,
            balance = balance
        )
        return UserMapper.toDomain(jpa.save(entity))
    }

    override fun findByUsername(username: String): User? =
        jpa.findByUsername(username)?.let { UserMapper.toDomain(it) }

    override fun findById(id: Long): User? =
        jpa.findById(id).orElse(null)?.let { UserMapper.toDomain(it) }

    override fun findAll(): List<User> =
        jpa.findAll().map { UserMapper.toDomain(it) }

    override fun update(entity: User) {
        jpa.save(UserMapper.toEntity(entity))
    }

    override fun deleteById(id: Long) =
        jpa.deleteById(id)

    override fun clear() =
        jpa.deleteAll()
}