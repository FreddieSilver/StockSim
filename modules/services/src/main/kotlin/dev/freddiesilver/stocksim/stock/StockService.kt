package dev.freddiesilver.stocksim.stock

import dev.freddiesilver.stocksim.CompanyRepository
import dev.freddiesilver.stocksim.StockRepository
import dev.freddiesilver.stocksim.stock.error.StockError
import dev.freddiesilver.stocksim.trading.stock.Stock
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class StockService(
    private val companyRepo: CompanyRepository,
    private val stockRepo: StockRepository,
) {
    fun createStock(
        companyId: Long,
        initialPrice: Double,
    ): Stock {
        val company = companyRepo.findById(companyId) ?: throw StockError.CompanyNotFound()
        val newStock = stockRepo.createStock(company, initialPrice.toBigDecimal())
        return newStock
    }

    fun getStockById(id: Long): Stock =
        stockRepo.findById(id) ?: throw StockError.StockNotFound()

    fun updateStockPrice(
        id: Long,
        newPrice: Double,
    ): Stock {
        stockRepo.updatePrice(id, newPrice.toBigDecimal())
        val updatedStock =
            stockRepo.findById(id)
                ?: throw StockError.StockNotFound()
        return updatedStock
    }
}
