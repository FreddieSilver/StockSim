package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.user.Email
import dev.freddiesilver.stocksim.user.PasswordValidationInfo
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import dev.freddiesilver.stocksim.user.auth.token.Token
import dev.freddiesilver.stocksim.user.auth.token.TokenValidationInfo
import java.time.Instant

interface UserRepository: Repository<User> {
    fun createUser(username: Username, email: Email, password: PasswordValidationInfo): User
    fun findByEmail(email: String): User?

    //auth

    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo):Pair<User, Token>?

    fun createToken(
        token: Token,
        maxTokens: Int
    ): Token

    fun updateTokenLastUsed(
        token: Token,
        now: Instant,
    )

    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int
}