package dev.freddiesilver.stocksim.user.auth

import dev.freddiesilver.stocksim.user.User

data class AuthenticatedUser(
    val user: User,
    val token: String,
)
