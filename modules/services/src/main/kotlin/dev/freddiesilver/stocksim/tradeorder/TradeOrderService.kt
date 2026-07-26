package dev.freddiesilver.stocksim.tradeorder

import dev.freddiesilver.stocksim.*
import dev.freddiesilver.stocksim.tradeorder.error.TradeOrderError
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.user.User
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class TradeOrderService(
    private val userRepo: UserRepository,
    private val stockRepo: StockRepository,
    private val holdingRepo: HoldingRepository,
    private val tradeOrderRepo: TradeOrderRepository
) {
    fun placeOrder(
        userId: Long,
        stockId: Long,
        type: OrderType,
        quantity: Int,
        totalPrice: Price,
    ): Either<TradeOrderError, TradeOrder> {
        if (quantity <= 0) {
            return failure(TradeOrderError.InvalidOrderDetails("Quantity must be greater than zero"))
        }

        val user = userRepo.findById(userId) ?: return failure(TradeOrderError.UserNotFound())
        val stock = stockRepo.findById(stockId) ?: return failure(TradeOrderError.StockNotFound())

        return when (type) {
            OrderType.BUY -> executeBuyOrder(user, stock, quantity, totalPrice)
            OrderType.SELL -> executeSellOrder(user, stock, quantity, totalPrice)
        }
    }

    private fun executeBuyOrder(
        user: User,
        stock: Stock,
        quantity: Int,
        totalPrice: Price,
    ): Either<TradeOrderError, TradeOrder> {
        if (user.balance.value < totalPrice.value) {
            return failure(TradeOrderError.InsufficientBalance("Required: ${totalPrice.value}, available: ${user.balance.value}"))
        }

        user.withdraw(totalPrice.value)
        userRepo.update(user)

        val holding = holdingRepo.findByUserAndStock(user.id, stock.id)
        if (holding != null) {
            holding.addQuantity(quantity)
            holdingRepo.update(holding)
        } else holdingRepo.createHolding(user.id, stock.id, quantity)
        val order =
            tradeOrderRepo.createOrder(
                user = user,
                stock = stock,
                type = OrderType.BUY,
                quantity = quantity,)
        return success(order)
    }

    private fun executeSellOrder(
        user: User,
        stock: Stock,
        quantity: Int,
        totalPrice: Price,
    ): Either<TradeOrderError, TradeOrder> {
        val holding =
            holdingRepo.findByUserAndStock(user.id, stock.id)
                ?: return failure(TradeOrderError.InsufficientHoldings("You do not own this stock"))

        if (holding.quantity < quantity) {
            return failure(TradeOrderError.InsufficientHoldings("Required: $quantity, owned: ${holding.quantity}"))
        }

            holding.removeQuantity(quantity)
            if (holding.quantity == 0) {
                holdingRepo.deleteById(holding.id)
            } else {
                holdingRepo.update(holding)
            }

            user.deposit(totalPrice.value)
            userRepo.update(user)

            val order =
                tradeOrderRepo.createOrder(
                    user = user,
                    stock = stock,
                    type = OrderType.SELL,
                    quantity = quantity,
                )
            return success(order)
    }
}
