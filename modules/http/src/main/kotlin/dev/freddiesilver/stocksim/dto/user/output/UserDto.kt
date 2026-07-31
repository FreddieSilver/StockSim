package dev.freddiesilver.stocksim.dto.user.output

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val balance: Double,
)
