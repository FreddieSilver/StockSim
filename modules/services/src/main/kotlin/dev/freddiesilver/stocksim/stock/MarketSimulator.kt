package dev.freddiesilver.stocksim.stock

import dev.freddiesilver.stocksim.PricePointRepository
import dev.freddiesilver.stocksim.StockRepository
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.Stock
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import kotlin.math.sqrt
import kotlin.random.Random

private val MIN_PRICE_VALUE = BigDecimal("0.01")

// 1800 ticks in an hour
private const val TICK_SCALER = 1800.0

//ticks
private const val MAX_HISTORY = 60
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
    private val stockRepo: StockRepository,
    private val pricePointRepo: PricePointRepository,
    private val random: Random,
    private val clock: Clock
) {

    /**
     * Advance the market by one tick. Returns the list of updated stocks.
     */
    fun simulateStep(): List<Stock> {
        val stocks = stockRepo.findAll()
        stocks.forEach { stock ->
            val volatility = stock.company.volatility
            val drift = stock.company.drift
            if (volatility != 0.0 || drift != 0.0) {
                val tickDrift = drift / TICK_SCALER
                val tickVolatility = volatility / sqrt(TICK_SCALER)

                val changePercent = tickDrift + random.nextDouble(-tickVolatility, tickVolatility)
                val multiplier = BigDecimal.ONE + changePercent.toBigDecimal()

                val newPrice = stock.price.value
                    .multiply(multiplier)
                    .setScale(2, RoundingMode.HALF_UP)
                    .max(MIN_PRICE_VALUE)

                stock.price = Price(newPrice)
                println("Ticker: ${stock.company.ticker.value} | Drift: $tickDrift | Volatility: $tickVolatility | Actual Roll: $changePercent | Current Price: ${stock.price.value}")
                pricePointRepo.createPricePoint(
                    stockId = stock.id,
                    price = newPrice,
                    time = clock.instant()
                )
            }
        }
        stockRepo.updateAllPrices(stocks)
        return stocks
    }
}
