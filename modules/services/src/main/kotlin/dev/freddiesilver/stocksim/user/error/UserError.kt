package dev.freddiesilver.stocksim.user.error

sealed class UserError(
    override val message: String,
) : Exception(message) {
    class UserAlreadyExists : UserError("User already exists")

    class UserNotFound : UserError("User not found")

    class InvalidUserData(
        additionalMessage: String,
    ) : UserError("Invalid user data: $additionalMessage")

    class InsufficientBalance(
        additionalMessage: String,
    ) : UserError("Insufficient balance: $additionalMessage")

    class InvalidDepositAmount(
        additionalMessage: String,
    ) : UserError("Invalid deposit amount: $additionalMessage")
}
