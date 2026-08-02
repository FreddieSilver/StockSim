package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.stock.PricePoint
import java.math.BigDecimal
import java.time.Instant

interface PricePointRepository: Repository<PricePoint> {
    fun createPricePoint(stockId: Long, price: BigDecimal, time: Instant): PricePoint

    fun findRecentByStockId(stockId: Long, limit:Int): List<PricePoint>

    // optimized findall by stock method
    fun findDownsampledByStockId(stockId: Long, targetPointCount: Int): List<PricePoint>
}