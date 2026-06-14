package dev.freddiesilver.stocksim.transaction

import dev.freddiesilver.stocksim.CompanyRepositoryMem
import dev.freddiesilver.stocksim.HoldingRepositoryMem
import dev.freddiesilver.stocksim.StockRepositoryMem
import dev.freddiesilver.stocksim.TradeOrderRepositoryMem
import dev.freddiesilver.stocksim.UserRepositoryMem

class TransactionManagerMem: TransactionManager {
    private val userRepo = UserRepositoryMem()
    private val stockRepo = StockRepositoryMem()
    private val tradeOrderRepo = TradeOrderRepositoryMem()
    private val holdingRepo = HoldingRepositoryMem()
    private val companyRepo = CompanyRepositoryMem()

    override fun <R> run(block: Transaction.() -> R): R =
        block(
            TransactionMem(
                userRepo = userRepo,
                stockRepo = stockRepo,
                tradeOrderRepo = tradeOrderRepo,
                holdingRepo = holdingRepo,
                companyRepo = companyRepo
            ),
        )
}