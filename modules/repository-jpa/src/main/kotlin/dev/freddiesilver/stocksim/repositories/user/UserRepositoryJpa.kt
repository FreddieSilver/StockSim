package dev.freddiesilver.stocksim.repositories.user

import dev.freddiesilver.stocksim.UserRepository
import dev.freddiesilver.stocksim.entities.UserEntity
import dev.freddiesilver.stocksim.mappers.UserMapper
import dev.freddiesilver.stocksim.user.Email
import dev.freddiesilver.stocksim.user.PasswordValidationInfo
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import dev.freddiesilver.stocksim.user.auth.token.Token
import dev.freddiesilver.stocksim.user.auth.token.TokenValidationInfo
import java.time.Instant

class UserRepositoryJpa(
    private val jpa: UserJpaRepository
) : UserRepository {

    override fun createUser(
        username: Username,
        email: Email,
        password: PasswordValidationInfo
    ): User {
        val entity = UserEntity(
            username = username.value,
            email = email.value,
            passwordValidationInfo = password.validationInfo,
            balance = java.math.BigDecimal.ZERO
        )
        return UserMapper.toDomain(jpa.save(entity))
    }

    override fun findByEmail(email: String): User? =
        jpa.findByEmail(email)?.let { UserMapper.toDomain(it) }

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

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? {
        TODO()
    }

    override fun createToken(token: Token, maxTokens: Int): Token {
        TODO()
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        TODO()
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        TODO()
    }
}
