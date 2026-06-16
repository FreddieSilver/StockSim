package dev.freddiesilver.stocksim.user

import java.math.BigDecimal

data class User(
    val id: Long = 0,
    val username: Username,
    val email: Email,
    val passwordValidationInfo: PasswordValidationInfo,
    var balance: Balance = Balance(STARTING_BALANCE_VALUE),
) {
    fun deposit(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Deposit amount must be positive" }
        this.balance = Balance(this.balance.value + amount)
    }

    fun withdraw(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Withdrawal amount must be positive" }
        if (this.balance.value < amount) {
            throw IllegalArgumentException("Insufficient balance")
        }
        this.balance = Balance(this.balance.value - amount)
    }

    companion object {
        private val STARTING_BALANCE_VALUE = BigDecimal(0)
    }
}
