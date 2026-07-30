package dev.freddiesilver.stocksim.event

import dev.freddiesilver.stocksim.trading.stock.Stock
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList

private const val SSE_EVENT_PRICE_UPDATE = "PRICE-UPDATE"

@Service
class SseService {
    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    fun subscribe(): SseEmitter {
        // 0L -> no timeout
        val emitter = SseEmitter(0L)
        emitters.add(emitter)
        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitters.remove(emitter) }
        emitter.onError { emitters.remove(emitter) }
        return emitter
    }

    // called by MarketTicker
    fun broadcastPriceUpdate(stocks: List<Stock>) {
        if (emitters.isEmpty()) return // no work

        val data = stocks.map { stock ->
            mapOf(
                "stock_id" to stock.id,
                "ticker" to stock.company.ticker.value,
                "price" to stock.price.value
            )
        }

        val deadEmitters = mutableListOf<SseEmitter>()

        // send the data to every connection
        emitters.forEach { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name(SSE_EVENT_PRICE_UPDATE)
                        .data(data)
                )
            } catch (_: Exception) {
                // mark for removal if the connection is dead
                deadEmitters.add(emitter)
            }
        }

        // remove dead
        emitters.removeAll(deadEmitters.toSet())
    }
}
