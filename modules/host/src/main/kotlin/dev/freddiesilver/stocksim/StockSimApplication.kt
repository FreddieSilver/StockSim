package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.repositories.company.CompanyJpaRepository
import dev.freddiesilver.stocksim.repositories.holding.HoldingJpaRepository
import dev.freddiesilver.stocksim.repositories.stock.StockJpaRepository
import dev.freddiesilver.stocksim.repositories.tradeorder.TradeOrderJpaRepository
import dev.freddiesilver.stocksim.repositories.user.TokenJpaRepository
import dev.freddiesilver.stocksim.repositories.user.UserJpaRepository
import dev.freddiesilver.stocksim.transaction.TransactionManagerJpa
import dev.freddiesilver.stocksim.transaction.TransactionManagerMem
import dev.freddiesilver.stocksim.user.auth.UsersDomainConfig
import dev.freddiesilver.stocksim.user.auth.token.Sha256TokenEncoder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.support.TransactionTemplate
import java.time.Clock
import java.time.Duration

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
        tokenJpaRepository: TokenJpaRepository,
        companyJpaRepository: CompanyJpaRepository,
        stockJpaRepository: StockJpaRepository,
        tradeOrderJpaRepository: TradeOrderJpaRepository,
        holdingJpaRepository: HoldingJpaRepository,
    ) = TransactionManagerJpa(
        transactionTemplate,
        userJpaRepository,
        tokenJpaRepository,
        companyJpaRepository,
        stockJpaRepository,
        tradeOrderJpaRepository,
        holdingJpaRepository,
    )

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean
    fun random(): kotlin.random.Random = kotlin.random.Random.Default

    @Bean
    fun usersDomainConfig() =
        UsersDomainConfig(
            tokenSizeInBytes = 256 / 8,
            tokenTtl = Duration.ofHours(24),
            tokenRollingTtl = Duration.ofHours(1),
            maxTokensPerUser = 3,
        )
}

fun main(args: Array<String>) {
    runApplication<StockSimApplication>(*args)
}
