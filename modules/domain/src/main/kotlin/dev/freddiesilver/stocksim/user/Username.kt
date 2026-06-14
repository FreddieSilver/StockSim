package dev.freddiesilver.stocksim.user

@JvmInline
value class Username(
    val value: String
){
    init {
        require(value.isNotBlank()) { "Username cannot be blank" }
        require(value.length <= 20) { "Username cannot exceed 20 characters" }
        require(value.matches(Regex("^[a-zA-Z0-9_]+$"))) { "Username can only contain letters, numbers, and underscores" }
    }

    override fun toString(): String = value
}