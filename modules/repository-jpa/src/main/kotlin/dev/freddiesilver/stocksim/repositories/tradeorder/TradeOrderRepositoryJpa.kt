package dev.freddiesilver.stocksim.repositories.tradeorder

import dev.freddiesilver.stocksim.TradeOrderRepository
import dev.freddiesilver.stocksim.mappers.TradeOrderMapper
import dev.freddiesilver.stocksim.repositories.stock.StockJpaRepository
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.repositories.user.UserJpaRepository

class TradeOrderRepositoryJpa(
    private val jpa: TradeOrderJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val stockJpaRepository: StockJpaRepository
) : TradeOrderRepository {

    override fun createOrder(
        user: User,
        stock: Stock,
        type: OrderType,
        quantity: Int
    ): TradeOrder {
        userJpaRepository.findById(user.id).orElseThrow {
            IllegalArgumentException("User with id ${user.id} not found")
        }
        stockJpaRepository.findById(stock.id).orElseThrow {
            IllegalArgumentException("Stock with id ${stock.id} not found")
        }
        val domainOrder = TradeOrder(
            user = user,
            stock = stock,
            type = type,
            quantity = quantity,
            priceValueAtOrder = stock.price.value,
            status = OrderStatus.PENDING
        )
        val entity = TradeOrderMapper.toEntity(domainOrder)
        return TradeOrderMapper.toDomain(jpa.save(entity))
    }

    override fun findByUserId(userId: Long): List<TradeOrder> =
        jpa.findByUserId(userId).map { TradeOrderMapper.toDomain(it) }

    override fun findByStockId(stockId: Long): List<TradeOrder> =
        jpa.findByStockId(stockId).map { TradeOrderMapper.toDomain(it) }

    override fun findByStatus(status: OrderStatus): List<TradeOrder> =
        jpa.findByStatus(status).map { TradeOrderMapper.toDomain(it) }

    override fun findById(id: Long): TradeOrder? =
        jpa.findById(id).orElse(null)?.let { TradeOrderMapper.toDomain(it) }

    override fun findAll(): List<TradeOrder> =
        jpa.findAll().map { TradeOrderMapper.toDomain(it) }

    override fun update(entity: TradeOrder) {
        val existing = jpa.findById(entity.id).orElseThrow {
            IllegalArgumentException("TradeOrder with id ${entity.id} not found")
        }
        existing.status = entity.status
        jpa.save(existing)
    }

    override fun deleteById(id: Long) =
        jpa.deleteById(id)

    override fun clear() =
        jpa.deleteAll()
}
