package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.tradeorder.TradeOrderRequestDto
import dev.freddiesilver.stocksim.helpers.errorResponse
import dev.freddiesilver.stocksim.helpers.messageResponse
import dev.freddiesilver.stocksim.stock.StockService
import dev.freddiesilver.stocksim.tradeorder.TradeOrderService
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.user.auth.AuthenticatedUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TradeOrderController(
    private val tradeOrderService: TradeOrderService,
    private val stockService: StockService,
) {
    @PostMapping("/trade-orders/place")
    fun placeOrder(
        user: AuthenticatedUser,
        @RequestBody input: TradeOrderRequestDto
    ): ResponseEntity<*> {
        val type = try { OrderType.valueOf(input.type.uppercase()) } catch (_: IllegalArgumentException) {
            return errorResponse("Invalid order type: ${input.type}", HttpStatus.BAD_REQUEST)
        }
        val stock = stockService.getStockById(input.stockId)
        val currentPrice = stock.price.value
        val totalPrice = currentPrice.multiply(input.quantity.toBigDecimal())

        tradeOrderService.placeOrder(
            userId = user.user.id,
            stockId = stock.id,
            type = type,
            quantity = input.quantity.toBigDecimal(),
            totalPrice = totalPrice
        )

        return messageResponse("Order placed successfully", HttpStatus.OK)
    }

    @GetMapping("/trade-orders")
    fun getTradeOrders(
        user: AuthenticatedUser
    ): ResponseEntity<*> {
        val tradeOrders = tradeOrderService.getTradeOrdersForUser(user.user.id)
        return ResponseEntity.ok(tradeOrders)
    }
}