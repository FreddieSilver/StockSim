package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import dev.freddiesilver.stocksim.user.Email
import dev.freddiesilver.stocksim.user.PasswordValidationInfo
import dev.freddiesilver.stocksim.user.auth.token.Token
import dev.freddiesilver.stocksim.user.auth.token.TokenValidationInfo
import java.time.Instant

class UserRepositoryMem: UserRepository {
    private val users = mutableListOf<User>()
    private val tokens = mutableListOf<Token>()
    private var nextId = 1L

    override fun createUser(
        username: Username,
        email: Email,
        password: PasswordValidationInfo,
    ): User = User(
        id = nextId++,
        username = username,
        email = email,
        passwordValidationInfo = password,
    ).also{
        users.add(it)
    }

    override fun findByEmail(email: String): User? =
        users.firstOrNull { it.email.value == email }


    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        tokens.firstOrNull { it.tokenValidationInfo == tokenValidationInfo }?.let {
            val user = findById(it.userId)
            requireNotNull(user)
            user to it
        }

    override fun createToken(
        token: Token,
        maxTokens: Int,
    ): Token {
        val nrOfTokens = tokens.count { it.userId == token.userId }

        if (nrOfTokens >= maxTokens) {
            tokens
                .filter { it.userId == token.userId }
                .minByOrNull { it.lastUsedAt }!!
                .also { tk -> tokens.removeIf { it.tokenValidationInfo == tk.tokenValidationInfo } }
        }
        tokens.add(token)
        return token
    }

    override fun updateTokenLastUsed(
        token: Token,
        now: Instant,
    ) {
        tokens.removeIf { it.tokenValidationInfo == token.tokenValidationInfo }
        token.lastUsedAt = now
        tokens.add(token)
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        val count = tokens.count { it.tokenValidationInfo == tokenValidationInfo }
        tokens.removeAll { it.tokenValidationInfo == tokenValidationInfo }
        return count
    }

    override fun findById(id: Long): User? =
        users.firstOrNull { it.id == id }


    override fun findAll(): List<User> =
        users.toList()

    override fun update(entity: User){
        if (entity.id == 0L) {
            val newUser = User(
                id = nextId++,
                username = entity.username,
                email = entity.email,
                passwordValidationInfo = entity.passwordValidationInfo,
                balance = entity.balance
            )
            users.add(newUser)
        } else {
            users.removeIf { it.id == entity.id }
            users.add(entity)
        }
    }

    override fun deleteById(id: Long) {
        users.removeIf { it.id == id }
    }

    override fun clear() =
        users.clear()
}