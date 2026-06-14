package dev.freddiesilver.stocksim.transaction

import dev.freddiesilver.stocksim.CompanyRepository
import dev.freddiesilver.stocksim.HoldingRepository
import dev.freddiesilver.stocksim.StockRepository
import dev.freddiesilver.stocksim.TradeOrderRepository
import dev.freddiesilver.stocksim.UserRepository

interface Transaction {
    val userRepo : UserRepository
    val stockRepo : StockRepository
    val tradeOrderRepo: TradeOrderRepository
    val holdingRepo: HoldingRepository
    val companyRepo: CompanyRepository

    fun rollback()
}
