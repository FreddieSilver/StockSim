package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.PricePoint
import java.math.BigDecimal
import java.time.Instant

class PricePointRepositoryMem: PricePointRepository {
    private val history = mutableListOf<PricePoint>()
    private var nextId = 1L
    override fun createPricePoint(stockId: Long, price: BigDecimal, time: Instant): PricePoint {
        val newPoint = PricePoint(
            id = nextId++,
            stockId = stockId,
            price = Price(price),
            timestamp = time
        )
        history.add(newPoint)
        return newPoint
    }

    override fun findRecentByStockId(
        stockId: Long,
        limit: Int
    ): List<PricePoint> = history.filter { it.stockId == stockId }.
    sortedByDescending { it.timestamp }.take(limit).reversed()


    override fun findById(id: Long): PricePoint? =
        history.find { it.id == id }

    override fun findAll(): List<PricePoint> =
        history.toList()

    override fun update(entity: PricePoint) {
        if (entity.id == 0L) {
            val newPricePoint = entity.copy(id = history.size + 1L)
            history.add(newPricePoint)
        } else {
            history.removeIf { it.id == entity.id }
            history.add(entity)
        }
    }

    override fun deleteById(id: Long) {
        history.removeIf { it.id == id }
    }

    override fun clear() = history.clear()


}