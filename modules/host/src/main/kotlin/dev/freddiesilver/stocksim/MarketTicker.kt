package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.stock.MarketSimulator
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MarketTicker(
    private val marketSimulator: MarketSimulator
) {
    @Scheduled(fixedRate = 2000) // 2 sec
    fun tick() {
        try {
            val updatedStocks = marketSimulator.simulateStep()

            // TODO: later, we will inject a Controller here and pass 'updatedStocks' to it so it can broadcast the new prices to the frontend via SSE

        } catch (e: Exception) {
            System.err.println("Market tick failed: ${e.message}")
        }
    }
}