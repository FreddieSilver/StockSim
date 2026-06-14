package dev.freddiesilver.stocksim.transaction

interface TransactionManager {
    fun <R> run(block: Transaction.() -> R): R
}
