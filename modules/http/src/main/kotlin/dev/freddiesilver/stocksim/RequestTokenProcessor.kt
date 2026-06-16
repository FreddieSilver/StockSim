package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.user.AuthService
import dev.freddiesilver.stocksim.user.auth.AuthenticatedUser
import org.springframework.stereotype.Component


@Component
class RequestTokenProcessor(
    val authService: AuthService
) {
    fun processAuthorizationHeaderValue(authorizationValue: String?): AuthenticatedUser? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME) {
            return null
        }
        return authService.getUserByToken(parts[1])?.let {
            AuthenticatedUser(
                it,
                parts[1],
            )
        }
    }

    companion object {
        const val SCHEME = "bearer"
    }
}
