package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.user.auth.UsersDomainConfig
import dev.freddiesilver.stocksim.user.auth.token.Sha256TokenEncoder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Clock
import java.time.Duration

@SpringBootApplication
class StockSimApplication {



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
