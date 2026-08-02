package dev.freddiesilver.stocksim.tradeorder

import dev.freddiesilver.stocksim.HoldingRepository
import dev.freddiesilver.stocksim.StockRepository
import dev.freddiesilver.stocksim.TradeOrderRepository
import dev.freddiesilver.stocksim.UserRepository
import dev.freddiesilver.stocksim.tradeorder.error.TradeOrderError
import dev.freddiesilver.stocksim.trading.holding.Holding
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.user.User
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Clock

@Service
@Transactional
class TradeOrderService(
    private val userRepo: UserRepository,
    private val stockRepo: StockRepository,
    private val holdingRepo: HoldingRepository,
    private val tradeOrderRepo: TradeOrderRepository,
    private val clock: Clock
) {
    fun placeOrder(
        userId: Long,
        stockId: Long,
        type: OrderType,
        quantity: BigDecimal,
        totalPrice: BigDecimal,
    ): TradeOrder {
        if (quantity <= BigDecimal.ZERO) {
            throw TradeOrderError.InvalidOrderDetails("Quantity must be greater than zero")
        }
        val user = userRepo.findById(userId) ?: throw TradeOrderError.UserNotFound()
        val stock = stockRepo.findById(stockId) ?: throw TradeOrderError.StockNotFound()

        return when (type) {
            OrderType.BUY -> executeBuyOrder(user, stock, quantity, totalPrice)
            OrderType.SELL -> executeSellOrder(user, stock, quantity, totalPrice)
        }
    }

    fun getTradeOrdersForUser(userId: Long): List<TradeOrder> {
        val user = userRepo.findById(userId) ?: throw TradeOrderError.UserNotFound()
        return tradeOrderRepo.findByUserId(user.id)
    }

    fun getHoldingsForUser(userId: Long): List<Holding> {
        val user = userRepo.findById(userId) ?: throw TradeOrderError.UserNotFound()
        val holdings = holdingRepo.findByUserId(user.id)
        return holdings
    }

    private fun executeBuyOrder(
        user: User,
        stock: Stock,
        quantity: BigDecimal,
        totalPrice: BigDecimal,
    ): TradeOrder {
        if (user.balance.value < totalPrice) {
            throw TradeOrderError.InsufficientBalance(
                "Required: ${totalPrice}, available: ${user.balance.value}",
            )
        }

        user.withdraw(totalPrice)
        userRepo.update(user)

        val holding = holdingRepo.findByUserIdAndStockId(user.id, stock.id)
        if (holding != null) {
            holding.addQuantity(quantity)
            holdingRepo.update(holding)
        } else {
            holdingRepo.createHolding(user.id, stock.id, quantity)
        }
        val order =
            tradeOrderRepo.createOrder(
                userId = user.id,
                stockId = stock.id,
                type = OrderType.BUY,
                quantity = quantity,
                time = clock.instant()
            )
        return order
    }

    private fun executeSellOrder(
        user: User,
        stock: Stock,
        quantity: BigDecimal,
        totalPrice: BigDecimal,
    ): TradeOrder {
        val holding =
            holdingRepo.findByUserIdAndStockId(user.id, stock.id)
                ?: throw TradeOrderError.InsufficientHoldings("You do not own this stock")

        if (holding.quantity < quantity) {
            throw TradeOrderError.InsufficientHoldings(
                "Required: $quantity, owned: ${holding.quantity}",
            )
        }
        holding.removeQuantity(quantity)
        holdingRepo.update(holding)

        user.deposit(totalPrice)
        userRepo.update(user)

        val order =
            tradeOrderRepo.createOrder(
                userId = user.id,
                stockId = stock.id,
                type = OrderType.SELL,
                quantity = quantity,
                time = clock.instant()
            )
        return order
    }
}
