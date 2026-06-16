package dev.freddiesilver.stocksim.repositories.user

import dev.freddiesilver.stocksim.UserRepository
import dev.freddiesilver.stocksim.entities.UserEntity
import dev.freddiesilver.stocksim.mappers.TokenMapper
import dev.freddiesilver.stocksim.mappers.UserMapper
import dev.freddiesilver.stocksim.user.Email
import dev.freddiesilver.stocksim.user.PasswordValidationInfo
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import dev.freddiesilver.stocksim.user.auth.token.Token
import dev.freddiesilver.stocksim.user.auth.token.TokenValidationInfo
import java.math.BigDecimal
import java.time.Instant

class UserRepositoryJpa(
    private val jpa: UserJpaRepository,
    private val tokenJpa: TokenJpaRepository,
) : UserRepository {
    // user

    override fun createUser(
        username: Username,
        email: Email,
        password: PasswordValidationInfo,
    ): User {
        val entity =
            UserEntity(
                username = username.value,
                email = email.value,
                passwordValidationInfo = password.validationInfo,
                balance = BigDecimal.ZERO,
            )
        return UserMapper.toDomain(jpa.save(entity))
    }

    override fun findByEmail(email: String): User? = jpa.findByEmail(email)?.let { UserMapper.toDomain(it) }

    override fun findById(id: Long): User? = jpa.findById(id).orElse(null)?.let { UserMapper.toDomain(it) }

    override fun findAll(): List<User> = jpa.findAll().map { UserMapper.toDomain(it) }

    override fun update(entity: User) {
        jpa.save(UserMapper.toEntity(entity))
    }

    override fun deleteById(id: Long) = jpa.deleteById(id)

    override fun clear() {
        tokenJpa.deleteAll() // tokens reference users, delete first
        jpa.deleteAll()
    }

    // --- Token ---

    override fun createToken(
        token: Token,
        maxTokens: Int,
    ): Token {
        val currentCount = tokenJpa.countByUserId(token.userId)

        if (currentCount >= maxTokens) {
            // evict the oldest token by last used
            val oldest = tokenJpa.findAllByUserIdOrderByLastUsedAtAsc(token.userId).firstOrNull()
            if (oldest != null) {
                tokenJpa.deleteByTokenValidationInfo(oldest.tokenValidationInfo)
            }
        }

        return TokenMapper.toDomain(tokenJpa.save(TokenMapper.toEntity(token)))
    }

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? {
        val tokenEntity =
            tokenJpa.findByTokenValidationInfo(tokenValidationInfo.validationInfo)
                ?: return null
        val userEntity =
            jpa.findById(tokenEntity.id).orElse(null)
                ?: return null
        return UserMapper.toDomain(userEntity) to TokenMapper.toDomain(tokenEntity)
    }

    override fun updateTokenLastUsed(
        token: Token,
        now: Instant,
    ) {
        val entity =
            tokenJpa.findByTokenValidationInfo(token.tokenValidationInfo.validationInfo)
                ?: return
        entity.lastUsedAt = now
        tokenJpa.save(entity)
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int =
        tokenJpa.deleteByTokenValidationInfo(tokenValidationInfo.validationInfo).toInt()
}
