package dev.freddiesilver.stocksim.dto.`in`

data class UserCreateDto(
    val username: String,
    val email: String,
    val password: String
)