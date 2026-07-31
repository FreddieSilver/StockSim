package dev.freddiesilver.stocksim.repositories.tradeorder

import dev.freddiesilver.stocksim.TradeOrderRepository
import dev.freddiesilver.stocksim.entities.TradeOrderEntity
import dev.freddiesilver.stocksim.repositories.stock.StockJpaRepository
import dev.freddiesilver.stocksim.repositories.user.UserJpaRepository
import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import java.math.BigDecimal

class TradeOrderRepositoryJpa(
    private val jpa: TradeOrderJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val stockJpaRepository: StockJpaRepository,
) : TradeOrderRepository {
    override fun createOrder(
        userId: Long,
        stockId: Long,
        type: OrderType,
        quantity: BigDecimal,
    ): TradeOrder {
        val userProxy = userJpaRepository.getReferenceById(userId)
        val stockProxy = stockJpaRepository.getReferenceById(stockId)
        val tradeOrder =
            TradeOrderEntity(
                user = userProxy,
                stock = stockProxy,
                type = type,
                quantity = quantity,
                priceAtOrder = stockProxy.price,
                status = OrderStatus.COMPLETED,
            )
        return jpa.save(tradeOrder).toDomain()
    }

    override fun findByUserId(userId: Long): List<TradeOrder> = jpa.findByUserId(userId).map { it.toDomain() }

    override fun findByStockId(stockId: Long): List<TradeOrder> = jpa.findByStockId(stockId).map { it.toDomain() }

    override fun findByStatus(status: OrderStatus): List<TradeOrder> = jpa.findByStatus(status).map { it.toDomain() }

    override fun findById(id: Long): TradeOrder? = jpa.findById(id).orElse(null)?.toDomain()

    override fun findAll(): List<TradeOrder> = jpa.findAll().map { it.toDomain() }

    override fun update(entity: TradeOrder) {
        val existing =
            jpa.findById(entity.id).orElseThrow {
                IllegalArgumentException("TradeOrder with id ${entity.id} not found")
            }
        existing.status = entity.status
        jpa.save(existing)
    }

    override fun deleteById(id: Long) = jpa.deleteById(id)

    override fun clear() = jpa.deleteAll()
}
