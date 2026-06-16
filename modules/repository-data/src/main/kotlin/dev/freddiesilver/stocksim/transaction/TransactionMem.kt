package dev.freddiesilver.stocksim.transaction

import dev.freddiesilver.stocksim.CompanyRepository
import dev.freddiesilver.stocksim.HoldingRepository
import dev.freddiesilver.stocksim.StockRepository
import dev.freddiesilver.stocksim.TradeOrderRepository
import dev.freddiesilver.stocksim.UserRepository

class TransactionMem(
    override val userRepo: UserRepository,
    override val stockRepo: StockRepository,
    override val tradeOrderRepo: TradeOrderRepository,
    override val holdingRepo: HoldingRepository,
    override val companyRepo: CompanyRepository,
) : Transaction {
    override fun rollback() = throw UnsupportedOperationException()
}
