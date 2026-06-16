package dev.freddiesilver.stocksim.transaction

import dev.freddiesilver.stocksim.repositories.company.CompanyJpaRepository
import dev.freddiesilver.stocksim.repositories.holding.HoldingJpaRepository
import dev.freddiesilver.stocksim.repositories.stock.StockJpaRepository
import dev.freddiesilver.stocksim.repositories.tradeorder.TradeOrderJpaRepository
import dev.freddiesilver.stocksim.repositories.user.TokenJpaRepository
import dev.freddiesilver.stocksim.repositories.user.UserJpaRepository
import org.springframework.transaction.support.TransactionTemplate

class TransactionManagerJpa(
    private val transactionTemplate: TransactionTemplate,
    private val userJpaRepository: UserJpaRepository,
    private val tokenJpaRepository: TokenJpaRepository,
    private val companyJpaRepository: CompanyJpaRepository,
    private val stockJpaRepository: StockJpaRepository,
    private val tradeOrderJpaRepository: TradeOrderJpaRepository,
    private val holdingJpaRepository: HoldingJpaRepository
) : TransactionManager {

    override fun <R> run(block: Transaction.() -> R): R =
        transactionTemplate.execute { status ->
            val transaction = TransactionJpa(
                status = status,
                userJpaRepository = userJpaRepository,
                tokenJpaRepository = tokenJpaRepository,
                companyJpaRepository = companyJpaRepository,
                stockJpaRepository = stockJpaRepository,
                tradeOrderJpaRepository = tradeOrderJpaRepository,
                holdingJpaRepository = holdingJpaRepository
            )
            block(transaction)
        } ?: throw IllegalStateException("Transaction returned null")
}
