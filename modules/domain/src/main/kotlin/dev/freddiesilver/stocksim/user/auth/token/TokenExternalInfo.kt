package dev.freddiesilver.stocksim.user.auth.token

import java.time.Instant

data class TokenExternalInfo(
    val tokenValue: String,
    val tokenExpiration: Instant,
)
