package dev.freddiesilver.stocksim.tradeorder

import dev.freddiesilver.stocksim.Either
import dev.freddiesilver.stocksim.failure
import dev.freddiesilver.stocksim.success
import dev.freddiesilver.stocksim.trading.holding.Holding
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.transaction.Transaction
import dev.freddiesilver.stocksim.transaction.TransactionManager
import dev.freddiesilver.stocksim.user.User
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TradeOrderService(private val trxManager: TransactionManager) {

    fun placeOrder(
        userId: Long,
        stockId: Long,
        type: OrderType,
        quantity: Int,
        totalPrice: Price
    ): Either<TradeOrderError, TradeOrder> =
        trxManager.run {
            if (quantity <= 0) {
                return@run failure(TradeOrderError.InvalidOrderDetails("Quantity must be greater than zero"))
            }

            val user = userRepo.findById(userId)
                ?: return@run failure(TradeOrderError.UserNotFound())
            val stock = stockRepo.findById(stockId)
                ?: return@run failure(TradeOrderError.StockNotFound())

            return@run when (type) {
                OrderType.BUY -> executeBuyOrder(user, stock, quantity, totalPrice)
                OrderType.SELL -> executeSellOrder(user, stock, quantity, totalPrice)
            }
        }

    private fun Transaction.executeBuyOrder(
        user: User,
        stock: Stock,
        quantity: Int,
        totalPrice: Price
    ): Either<TradeOrderError, TradeOrder> {
        return try {
            if (user.balance.value < totalPrice.value) {
                return failure(TradeOrderError.InsufficientBalance("Required: ${totalPrice.value}, available: ${user.balance.value}"))
            }

            user.withdraw(totalPrice.value)
            userRepo.update(user)

            val holding = holdingRepo.findByUserAndStock(user.id, stock.id)
            if (holding != null) {
                holding.addQuantity(quantity)
                holdingRepo.update(holding)
            } else {
                holdingRepo.createHolding(user.id, stock.id, quantity)
            }

            val order = tradeOrderRepo.createOrder(
                user = user,
                stock = stock,
                type = OrderType.BUY,
                quantity = quantity
            )
            success(order)
        } catch (e: Exception) {
            rollbackBuy(user, stock.id, quantity, totalPrice.value)
            failure(TradeOrderError.InvalidOrderDetails(e.message ?: "Unknown error during BUY order"))
        }
    }

    private fun Transaction.executeSellOrder(
        user: User,
        stock: Stock,
        quantity: Int,
        totalPrice: Price
    ): Either<TradeOrderError, TradeOrder> {
        val holding = holdingRepo.findByUserAndStock(user.id, stock.id)
            ?: return failure(TradeOrderError.InsufficientHoldings("You do not own this stock"))

        if (holding.quantity < quantity) {
            return failure(TradeOrderError.InsufficientHoldings("Required: $quantity, owned: ${holding.quantity}"))
        }

        return try {
            holding.removeQuantity(quantity)
            if (holding.quantity == 0)
                holdingRepo.deleteById(holding.id)
            else
                holdingRepo.update(holding)

            user.deposit(totalPrice.value)
            userRepo.update(user)

            val order = tradeOrderRepo.createOrder(
                user = user,
                stock = stock,
                type = OrderType.SELL,
                quantity = quantity
            )
            success(order)
        } catch (e: Exception) {
            rollbackSell(user, holding, quantity, totalPrice.value)
            failure(TradeOrderError.InvalidOrderDetails(e.message ?: "Unknown error during SELL order"))
        }
    }

    private fun Transaction.rollbackBuy(user: User, stockId: Long, quantity: Int, amount: BigDecimal) {
        try {
            user.deposit(amount)
            userRepo.update(user)
            holdingRepo.findByUserAndStock(user.id, stockId)?.let { holding ->
                if (holding.quantity == quantity) {
                    holdingRepo.deleteById(holding.id)
                } else {
                    holding.removeQuantity(quantity)
                    holdingRepo.update(holding)
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun Transaction.rollbackSell(user: User, holding: Holding, quantity: Int, amount: BigDecimal) {
        try {
            holding.addQuantity(quantity)
            holdingRepo.update(holding)
            user.withdraw(amount)
            userRepo.update(user)
        } catch (_: Exception) {
        }
    }
}
