package dev.freddiesilver.stocksim.user.error

sealed class AuthError(
    override val message: String,
) : Exception(message) {
    class UserOrPasswordAreInvalid : AuthError("User or password are invalid")

    class BadPassword : AuthError("Password is invalid")

    class EmailInUse : AuthError("This email is already in use")
}
