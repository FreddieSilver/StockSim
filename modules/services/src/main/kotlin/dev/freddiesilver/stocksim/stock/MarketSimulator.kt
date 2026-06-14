package dev.freddiesilver.stocksim.stock

import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.transaction.TransactionManager
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

/**
 * Simulates market price fluctuations using per-stock volatility and drift.
 *
 * Each call to [simulateStep] applies a random percentage change to every stock's price:
 *   newPrice = currentPrice * (1 + drift + random(-volatility, +volatility))
 *
 * Prices are floored at $0.01 and rounded to 2 decimal places.
 */
@Service
class MarketSimulator(
    private val trxManager: TransactionManager,
    private val random: Random = Random.Default
) {

    /**
     * Advance the market by one tick. Returns the list of updated stocks.
     */
    fun simulateStep(): List<Stock> =
        trxManager.run {
            val stocks = stockRepo.findAll()
            stocks.map { stock ->
                val volatility = stock.company.volatility
                val drift = stock.company.drift
                val newPrice = if (volatility == 0.0 && drift == 0.0) {
                    stock.price.value
                } else {
                    val changePercent = drift + random.nextDouble(-volatility, volatility)
                    val multiplier = BigDecimal.ONE + changePercent.toBigDecimal()
                    stock.price.value
                        .multiply(multiplier)
                        .setScale(2, RoundingMode.HALF_UP)
                        .max(MIN_PRICE)
                }
                stockRepo.updatePrice(stock.id, newPrice)
                stockRepo.findById(stock.id)!!
            }
        }

    companion object {
        val MIN_PRICE: BigDecimal = BigDecimal("0.01")
    }
}
