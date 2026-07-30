package dev.freddiesilver.stocksim.dto.user.output

data class UserHomeResponseDto(
    val id: Long,
    val username: String,
    val email: String,
    val balance: Double,
)
