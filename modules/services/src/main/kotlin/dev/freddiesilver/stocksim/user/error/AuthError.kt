package dev.freddiesilver.stocksim.user.error

sealed class AuthError(
    override val message: String,
) : Exception(message) {
    class BadPassword : AuthError(
        "Password must have 8 or more characters and have a number as well as an uppercase and a lowercase letter.",
    )

    class EmailInUse : AuthError("This email is already in use")

    class CredentialsMissing : AuthError("Email and password must not be blank")

    class IncorrectCredentials : AuthError("Incorrect email or password")

    class InvalidRegistrationData : AuthError("Invalid registration data provided")
}
