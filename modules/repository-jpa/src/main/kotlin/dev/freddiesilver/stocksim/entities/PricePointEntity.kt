package dev.freddiesilver.stocksim.entities

import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.PricePoint
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "price_history")
class PricePointEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "stock_id", nullable = false)
    val stockId: Long,
    @Column(nullable = false, precision = 19, scale = 4)
    val price: BigDecimal,
    @Column(nullable = false)
    val timestamp: Instant
){
    fun toDomain(): PricePoint {
        return PricePoint(
            id = id,
            stockId = stockId,
            price = Price(price),
            timestamp = timestamp,
            )
    }
}