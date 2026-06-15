package dev.freddiesilver.stocksim.user.auth.token

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
