package dev.freddiesilver.stocksim.repositories.pricepoint

import dev.freddiesilver.stocksim.PricePointRepository
import dev.freddiesilver.stocksim.entities.PricePointEntity
import dev.freddiesilver.stocksim.entities.toJpaEntity
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
        return saved.toDomain()
    }

    override fun findRecentByStockId(
        stockId: Long,
        limit: Int
    ): List<PricePoint> {
        val page = PageRequest.of(0, limit)
        return jpa.findByStockIdOrderByTimestampDesc(stockId, page)
            .map{it.toDomain()}.reversed()
    }

    override fun findDownsampledByStockId(stockId: Long, targetPointCount: Int): List<PricePoint> {
        // find out how many total rows exist
        val totalRows = jpa.countByStockId(stockId)

        if (totalRows <= targetPointCount) {
            // if we have fewer rows than the target, just get them all normally
            val page = PageRequest.of(0, targetPointCount)
            return jpa.findByStockIdOrderByTimestampDesc(stockId, page)
                .map { it.toDomain() }.reversed()
        }

        // calculate the skip step (e.g., if total is 10,000 and target is 100, N = 100)
        val nthRow = (totalRows / targetPointCount).toInt()

        // let the database do the heavy lifting! Returns exactly ~100 rows.
        return jpa.findDownsampledHistory(stockId, nthRow)
            .map { it.toDomain() }
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