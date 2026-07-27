package dev.freddiesilver.stocksim.sse

import dev.freddiesilver.stocksim.event.SseService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StockStreamController(
    private val sseService: SseService
) {
    @GetMapping("/stocks/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamPrices() = sseService.subscribe()
}