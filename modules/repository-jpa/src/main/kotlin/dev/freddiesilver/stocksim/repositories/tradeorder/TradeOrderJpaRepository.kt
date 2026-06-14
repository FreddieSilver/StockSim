package dev.freddiesilver.stocksim.repositories.tradeorder

import dev.freddiesilver.stocksim.entities.TradeOrderEntity
import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository

interface TradeOrderJpaRepository : JpaRepository<TradeOrderEntity, Long> {
    fun findByUserId(userId: Long): List<TradeOrderEntity>
    fun findByStockId(stockId: Long): List<TradeOrderEntity>
    fun findByStatus(status: OrderStatus): List<TradeOrderEntity>
}
