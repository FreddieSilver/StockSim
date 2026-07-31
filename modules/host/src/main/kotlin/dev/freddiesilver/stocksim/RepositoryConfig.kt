package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.repositories.company.CompanyJpaRepository
import dev.freddiesilver.stocksim.repositories.company.CompanyRepositoryJpa
import dev.freddiesilver.stocksim.repositories.holding.HoldingJpaRepository
import dev.freddiesilver.stocksim.repositories.holding.HoldingRepositoryJpa
import dev.freddiesilver.stocksim.repositories.pricepoint.PricePointJpaRepository
import dev.freddiesilver.stocksim.repositories.pricepoint.PricePointRepositoryJpa
import dev.freddiesilver.stocksim.repositories.stock.StockJpaRepository
import dev.freddiesilver.stocksim.repositories.stock.StockRepositoryJpa
import dev.freddiesilver.stocksim.repositories.tradeorder.TradeOrderJpaRepository
import dev.freddiesilver.stocksim.repositories.tradeorder.TradeOrderRepositoryJpa
import dev.freddiesilver.stocksim.repositories.user.TokenJpaRepository
import dev.freddiesilver.stocksim.repositories.user.UserJpaRepository
import dev.freddiesilver.stocksim.repositories.user.UserRepositoryJpa
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class RepositoryConfig {
    @Bean
    @Profile("mem")
    fun userRepositoryMem(): UserRepository = UserRepositoryMem()

    @Bean
    @Profile("mem")
    fun companyRepositoryMem(): CompanyRepository = CompanyRepositoryMem()

    @Bean
    @Profile("mem")
    fun stockRepositoryMem(): StockRepository = StockRepositoryMem()

    @Bean
    @Profile("mem")
    fun pricePointRepositoryMem(): PricePointRepository = PricePointRepositoryMem()

    @Bean
    @Profile("mem")
    fun holdingRepositoryMem(
        userRepo: UserRepository,
        stockRepo: StockRepository
    ): HoldingRepository = HoldingRepositoryMem(userRepo, stockRepo)

    @Bean
    @Profile("mem")
    fun tradeOrderRepositoryMem(
        userRepo: UserRepository,
        stockRepo: StockRepository
    ): TradeOrderRepository  = TradeOrderRepositoryMem(userRepo, stockRepo)

    @Bean
    @Profile("jpa")
    fun userRepositoryJpa(
        userJpa: UserJpaRepository,
        tokenJpa: TokenJpaRepository,
    ): UserRepository = UserRepositoryJpa(userJpa, tokenJpa)

    @Bean
    @Profile("jpa")
    fun companyRepositoryJpa(companyJpa: CompanyJpaRepository): CompanyRepository = CompanyRepositoryJpa(companyJpa)

    @Bean
    @Profile("jpa")
    fun stockRepositoryJpa(
        stockJpa: StockJpaRepository,
        companyJpa: CompanyJpaRepository,
    ): StockRepository = StockRepositoryJpa(stockJpa, companyJpa)

    @Bean
    @Profile("jpa")
    fun pricePointRepositoryJpa(
        pricePointJpa: PricePointJpaRepository,
    ): PricePointRepository = PricePointRepositoryJpa(pricePointJpa)

    @Bean
    @Profile("jpa")
    fun holdingRepositoryJpa(
        holdingJpa: HoldingJpaRepository,
        userRepo: UserJpaRepository,
        stockRepo: StockJpaRepository
    ): HoldingRepository =
        HoldingRepositoryJpa(holdingJpa, userRepo, stockRepo)

    @Bean
    @Profile("jpa")
    fun tradeOrderRepositoryJpa(
        tradeOrderJpa: TradeOrderJpaRepository,
        userJpa: UserJpaRepository,
        stockJpa: StockJpaRepository,
    ): TradeOrderRepository = TradeOrderRepositoryJpa(tradeOrderJpa, userJpa, stockJpa)
}

