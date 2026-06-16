package dev.freddiesilver.stocksim.transaction

import dev.freddiesilver.stocksim.repositories.company.CompanyJpaRepository
import dev.freddiesilver.stocksim.repositories.company.CompanyRepositoryJpa
import dev.freddiesilver.stocksim.repositories.holding.HoldingJpaRepository
import dev.freddiesilver.stocksim.repositories.holding.HoldingRepositoryJpa
import dev.freddiesilver.stocksim.repositories.stock.StockJpaRepository
import dev.freddiesilver.stocksim.repositories.stock.StockRepositoryJpa
import dev.freddiesilver.stocksim.repositories.tradeorder.TradeOrderJpaRepository
import dev.freddiesilver.stocksim.repositories.tradeorder.TradeOrderRepositoryJpa
import dev.freddiesilver.stocksim.repositories.user.TokenJpaRepository
import dev.freddiesilver.stocksim.repositories.user.UserJpaRepository
import dev.freddiesilver.stocksim.repositories.user.UserRepositoryJpa
import org.springframework.transaction.TransactionStatus

class TransactionJpa(
    private val status: TransactionStatus,
    userJpaRepository: UserJpaRepository,
    tokenJpaRepository: TokenJpaRepository,
    companyJpaRepository: CompanyJpaRepository,
    stockJpaRepository: StockJpaRepository,
    tradeOrderJpaRepository: TradeOrderJpaRepository,
    holdingJpaRepository: HoldingJpaRepository,
) : Transaction {
    override val userRepo = UserRepositoryJpa(userJpaRepository, tokenJpaRepository)
    override val stockRepo = StockRepositoryJpa(stockJpaRepository, companyJpaRepository)
    override val tradeOrderRepo = TradeOrderRepositoryJpa(tradeOrderJpaRepository, userJpaRepository, stockJpaRepository)
    override val holdingRepo = HoldingRepositoryJpa(holdingJpaRepository)
    override val companyRepo = CompanyRepositoryJpa(companyJpaRepository)

    override fun rollback() {
        status.setRollbackOnly()
    }
}
