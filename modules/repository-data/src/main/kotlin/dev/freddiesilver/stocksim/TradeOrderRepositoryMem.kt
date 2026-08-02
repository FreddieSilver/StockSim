package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import java.math.BigDecimal
import java.time.Instant

class TradeOrderRepositoryMem(
    private val userRepo: UserRepository,
    private val stockRepo: StockRepository
) : TradeOrderRepository {
    private val orders = mutableListOf<TradeOrder>()
    private var nextId = 1L

    override fun createOrder(
        userId: Long,
        stockId: Long,
        type: OrderType,
        quantity: BigDecimal,
        time: Instant
    ): TradeOrder {
        val user = userRepo.findById(userId)
            ?: throw IllegalArgumentException("User not found with ID: $userId")
        val stock = stockRepo.findById(stockId)
            ?: throw IllegalArgumentException("Stock not found with ID: $stockId")
        return TradeOrder(
            id = nextId++,
            timestamp = time,
            user = user,
            stock = stock,
            type = type,
            quantity = quantity,
            priceValueAtOrder = stock.price.value,
            status = OrderStatus.COMPLETED,
        ).also { orders.add(it) }
    }

    override fun findByUserId(userId: Long): List<TradeOrder> = orders.filter { it.user.id == userId }

    override fun findByStockId(stockId: Long): List<TradeOrder> = orders.filter { it.stock.id == stockId }

    override fun findByStatus(status: OrderStatus): List<TradeOrder> = orders.filter { it.status == status }

    override fun findById(id: Long): TradeOrder? = orders.firstOrNull { it.id == id }

    override fun findAll(): List<TradeOrder> = orders.toList()

    override fun update(entity: TradeOrder) {
        if (entity.id == 0L) {
            val newOrder =
                TradeOrder(
                    id = nextId++,
                    timestamp = Instant.now(),
                    user = entity.user,
                    stock = entity.stock,
                    type = entity.type,
                    quantity = entity.quantity,
                    priceValueAtOrder = entity.priceValueAtOrder,
                    status = entity.status,
                )
            orders.add(newOrder)
        } else {
            orders.removeIf { it.id == entity.id }
            orders.add(entity)
        }
    }

    override fun deleteById(id: Long) {
        orders.removeIf { it.id == id }
    }

    override fun clear() = orders.clear()
}
