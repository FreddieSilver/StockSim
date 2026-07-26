package dev.freddiesilver.stocksim.dto.user.output

data class UserHomeResponseDto(
    val id: Long,
    val name: String,
    val email: String,
    val balance: Double,
)
