package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.event.SseService
import dev.freddiesilver.stocksim.stock.MarketSimulator
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MarketTicker(
    private val marketSimulator: MarketSimulator,
    private val sseService: SseService
) {
    @Scheduled(fixedRate = 2000) // 2 sec
    fun tick() {
        try {
            val updatedStocks = marketSimulator.simulateStep()
            sseService.broadcastPriceUpdate(updatedStocks)
        } catch (e: Exception) {
            System.err.println("Market tick failed: ${e.message}")
        }
    }
}
