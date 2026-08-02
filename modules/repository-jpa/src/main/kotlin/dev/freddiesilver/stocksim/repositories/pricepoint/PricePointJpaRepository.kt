package dev.freddiesilver.stocksim.repositories.pricepoint

import dev.freddiesilver.stocksim.entities.PricePointEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PricePointJpaRepository: JpaRepository<PricePointEntity, Long> {
    // Pageable to get the most recent points efficiently
    fun findByStockIdOrderByTimestampDesc(stockId: Long, pageable: Pageable): List<PricePointEntity>

    // So we don't have to get thousands of tuples
    //It numbers the rows 1, 2, 3... and only selects the row if (RowNumber / nthRow) has a remainder of 0.
    @Query(value = """
        SELECT id, stock_id, price, timestamp 
        FROM (
            SELECT id, stock_id, price, timestamp, 
                   row_number() OVER (ORDER BY timestamp) as rn 
            FROM price_history 
            WHERE stock_id = :stockId
        ) t 
        WHERE MOD(t.rn, :nthRow) = 0
    """, nativeQuery = true)
    fun findDownsampledHistory(
        @Param("stockId") stockId: Long,
        @Param("nthRow") nthRow: Int
    ): List<PricePointEntity>

    // helper to know how many rows exist so we can calculate 'N'
    fun countByStockId(stockId: Long): Long
}