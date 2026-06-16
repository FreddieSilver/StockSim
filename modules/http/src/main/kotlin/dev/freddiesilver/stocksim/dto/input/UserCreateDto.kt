package dev.freddiesilver.stocksim.dto.input

data class UserCreateDto(
    val username: String,
    val email: String,
    val password: String,
)
