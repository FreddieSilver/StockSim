package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.tradeorder.TradeOrderService
import dev.freddiesilver.stocksim.user.auth.AuthenticatedUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HoldingController(
    private val tradeOrderService: TradeOrderService
) {
    @GetMapping("/holdings")
    fun getHoldings(
        user: AuthenticatedUser
    ): ResponseEntity<*> {
        val tradeOrders = tradeOrderService.getHoldingsForUser(user.user.id)
        return ResponseEntity.ok(tradeOrders)
    }
}