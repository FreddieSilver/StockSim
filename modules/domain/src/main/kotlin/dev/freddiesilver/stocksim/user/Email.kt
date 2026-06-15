package dev.freddiesilver.stocksim.user

@JvmInline
value class Email(
    val value: String,
) {
    init {
        require(checkFormat(value)) { "E-mail is invalid" }
        require(value.isNotBlank()) { "E-mail can't be empty" }
    }

    companion object {
        fun checkFormat(email: String): Boolean {
            val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            return email.matches(regex)
        }
    }
}
