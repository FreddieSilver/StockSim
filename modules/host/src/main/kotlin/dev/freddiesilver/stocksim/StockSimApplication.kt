package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.repositories.company.CompanyJpaRepository
import dev.freddiesilver.stocksim.repositories.holding.HoldingJpaRepository
import dev.freddiesilver.stocksim.repositories.stock.StockJpaRepository
import dev.freddiesilver.stocksim.repositories.tradeorder.TradeOrderJpaRepository
import dev.freddiesilver.stocksim.transaction.TransactionManagerJpa
import dev.freddiesilver.stocksim.transaction.TransactionManagerMem
import dev.freddiesilver.stocksim.repositories.user.UserJpaRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.transaction.support.TransactionTemplate

@SpringBootApplication
class StockSimApplication {

    @Bean
    @Profile("mem")
    fun trxManagerMem() = TransactionManagerMem()

    @Bean
    @Profile("jpa")
    fun trxManagerJpa(
        transactionTemplate: TransactionTemplate,
        userJpaRepository: UserJpaRepository,
        companyJpaRepository: CompanyJpaRepository,
        stockJpaRepository: StockJpaRepository,
        tradeOrderJpaRepository: TradeOrderJpaRepository,
        holdingJpaRepository: HoldingJpaRepository
    ) = TransactionManagerJpa(
        transactionTemplate,
        userJpaRepository,
        companyJpaRepository,
        stockJpaRepository,
        tradeOrderJpaRepository,
        holdingJpaRepository
    )
}

fun main(args: Array<String>) {
    runApplication<StockSimApplication>(*args)
}
