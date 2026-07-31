package dev.freddiesilver.stocksim.dto

import java.time.Instant

data class ApiErrorResponse(
    val error: String,
    val status: Int,
    val timestamp: Instant = Instant.now(),
)