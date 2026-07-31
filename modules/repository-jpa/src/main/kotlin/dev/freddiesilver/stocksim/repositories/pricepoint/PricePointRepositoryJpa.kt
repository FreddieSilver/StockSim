package dev.freddiesilver.stocksim.repositories.pricepoint

import dev.freddiesilver.stocksim.PricePointRepository
import dev.freddiesilver.stocksim.entities.PricePointEntity
import dev.freddiesilver.stocksim.entities.toJpaEntity
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.PricePoint
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.time.Instant

class PricePointRepositoryJpa(
    private val jpa: PricePointJpaRepository
): PricePointRepository {
    override fun createPricePoint(stockId: Long, price: BigDecimal, time: Instant): PricePoint {
        val entity = PricePointEntity(
            id = 0,
            stockId = stockId,
            price = price,
            timestamp = time
        )
        val saved = jpa.save(entity)
        return PricePoint(
            id = saved.id,
            stockId = saved.stockId,
            timestamp = saved.timestamp,
            price = Price(
                saved.price
            )
        )
    }

    override fun findRecentByStockId(
        stockId: Long,
        limit: Int
    ): List<PricePoint> {
        val page = PageRequest.of(0, limit)
        return jpa.findByStockIdOrderByTimestampDesc(stockId, page)
            .map{it.toDomain()}.reversed()
    }

    override fun findById(id: Long): PricePoint? = jpa.findById(id).orElse(null)?.toDomain()

    override fun findAll(): List<PricePoint> =
        jpa.findAll().map{ it.toDomain() }

    override fun update(entity: PricePoint) {
        jpa.save(entity.toJpaEntity())
    }

    override fun deleteById(id: Long) = jpa.deleteById(id)

    override fun clear() = jpa.deleteAll()

}