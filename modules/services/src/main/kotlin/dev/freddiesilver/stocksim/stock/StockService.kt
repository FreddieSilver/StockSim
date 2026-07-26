package dev.freddiesilver.stocksim.stock

import dev.freddiesilver.stocksim.*
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
    ): Either<StockError, Stock> {
        val company = companyRepo.findById(companyId) ?: return failure(StockError.CompanyNotFound())
        val newStock = stockRepo.createStock(company, initialPrice.toBigDecimal())
        return success(newStock)
    }

    fun getStockById(id: Long): Either<StockError, Stock> =
            stockRepo.findById(id)?.let { stock ->
                success(stock)
            } ?: failure(StockError.StockNotFound())

    fun updateStockPrice(
        id: Long,
        newPrice: Double,
    ): Either<StockError, Stock> {
        stockRepo.updatePrice(id, newPrice.toBigDecimal())
        val updatedStock = stockRepo.findById(id)
            ?: return failure(StockError.StockNotFound())
        return success(updatedStock)
    }

}
