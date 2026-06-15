package dev.freddiesilver.stocksim.user.auth.token

import java.time.Instant

class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Long,
    val createdAt: Instant,
    var lastUsedAt: Instant,
)
