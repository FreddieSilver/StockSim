package dev.freddiesilver.stocksim.user

import dev.freddiesilver.stocksim.transaction.Transaction
import dev.freddiesilver.stocksim.user.auth.AuthenticatedUser
import dev.freddiesilver.stocksim.user.auth.UsersDomainConfig
import dev.freddiesilver.stocksim.Either
import dev.freddiesilver.stocksim.failure
import dev.freddiesilver.stocksim.success
import dev.freddiesilver.stocksim.transaction.TransactionManager
import dev.freddiesilver.stocksim.user.auth.token.Token
import dev.freddiesilver.stocksim.user.auth.token.TokenEncoder
import dev.freddiesilver.stocksim.user.error.AuthError
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Clock
import java.time.Duration
import java.util.Base64.getUrlDecoder
import java.util.Base64.getUrlEncoder

@Service
class AuthService(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UsersDomainConfig,
    private val trxManager: TransactionManager,
    private val clock: Clock,
) {

    fun registerUser(name: String, email: String, password: String): Either<AuthError, AuthenticatedUser> {
        if (!isSafePassword(password)) {
            return failure(AuthError.BadPassword())
        }

        return trxManager.run {
            if (userRepo.findByEmail(email) != null) {
                return@run failure(AuthError.EmailInUse())
            }

            val passwordInfo = PasswordValidationInfo(passwordEncoder.encode(password))
            val user = userRepo.createUser(Username(name), Email(email), passwordInfo)

            val token = createAndSaveToken(user.id)
            success(AuthenticatedUser(user, token.tokenValidationInfo.validationInfo))
        }
    }

    fun login(email: String, password: String): Either<AuthError, AuthenticatedUser> {
        if (email.isBlank() || password.isBlank()) return failure(AuthError.UserOrPasswordAreInvalid())

        return trxManager.run {
            val user = userRepo.findByEmail(email) ?: return@run failure(AuthError.UserOrPasswordAreInvalid())

            if (!passwordEncoder.matches(password, user.passwordValidationInfo.validationInfo)) {
                return@run failure(AuthError.UserOrPasswordAreInvalid())
            }

            val token = createAndSaveToken(user.id)
            success(AuthenticatedUser(user, token.tokenValidationInfo.validationInfo))
        }
    }

    fun getUserByToken(tokenString: String): User? {
        if (!isValidTokenFormat(tokenString)) return null

        return trxManager.run {
            val tokenInfo = tokenEncoder.createValidationInformation(tokenString)
            val userAndToken = userRepo.getTokenByTokenValidationInfo(tokenInfo) ?: return@run null

            val token = userAndToken.second
            if (isTokenTimeValid(token)) {
                userRepo.updateTokenLastUsed(token, clock.instant())
                userAndToken.first
            } else {
                null
            }
        }
    }

    fun revokeToken(tokenString: String): Boolean {
        return trxManager.run {
            val tokenInfo = tokenEncoder.createValidationInformation(tokenString)
            userRepo.removeTokenByValidationInfo(tokenInfo)
            true
        }
    }

    // helpers

    private fun Transaction.createAndSaveToken(userId: Long): Token {
        val tokenValue = generateSecureTokenString()
        val now = clock.instant()
        val token = Token(
            tokenValidationInfo = tokenEncoder.createValidationInformation(tokenValue),
            userId = userId,
            createdAt = now,
            lastUsedAt = now,
        )
        userRepo.createToken(token, config.maxTokensPerUser)
        return token
    }

    private fun isSafePassword(password: String) =
        password.length >= 8 &&
                password.any { it.isDigit() } &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() }

    private fun generateSecureTokenString(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            getUrlEncoder().withoutPadding().encodeToString(byteArray)
        }

    private fun isValidTokenFormat(token: String): Boolean =
        try {
            getUrlDecoder().decode(token).size == config.tokenSizeInBytes
            true
        } catch (_: IllegalArgumentException) {
            false
        }

    private fun isTokenTimeValid(token: Token): Boolean {
        val now = clock.instant()
        return token.createdAt <= now &&
                Duration.between(token.createdAt, now) <= config.tokenTtl &&
                Duration.between(token.lastUsedAt, now) <= config.tokenRollingTtl
    }
}