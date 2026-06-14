package dev.freddiesilver.stocksim.stock

import dev.freddiesilver.stocksim.Either
import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import dev.freddiesilver.stocksim.failure
import dev.freddiesilver.stocksim.success
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.transaction.TransactionManager
import org.springframework.stereotype.Service

@Service
class StockService(private val trxManager: TransactionManager) {

    fun createStock(ticker: String, companyName: String, initialPrice: Double): Either<StockError, Stock> =
        trxManager.run {
            try {
                val company = Company(
                    id = 0L,
                    name = CompanyName(companyName),
                    ticker = Ticker(ticker),
                    description = Description("No description provided"),
                    volatility = 0.02,
                    drift = 0.001
                )
                val newStock = stockRepo.createStock(ticker, company, initialPrice.toBigDecimal())
                success(newStock)
            } catch (e: Exception) {
                failure(StockError.InvalidStockData(e.message ?: "Unknown error"))
            }
        }

    fun getStockById(id: Long): Either<StockError, Stock> =
        trxManager.run {
            stockRepo.findById(id)?.let { stock ->
                success(stock)
            } ?: failure(StockError.StockNotFound())
        }

    fun updateStockPrice(id: Long, newPrice: Double): Either<StockError, Stock> =
        trxManager.run {
            try {
                stockRepo.updatePrice(id, newPrice.toBigDecimal())
                val updatedStock = stockRepo.findById(id)
                    ?: return@run failure(StockError.StockNotFound())
                success(updatedStock)
            } catch (e: Exception) {
                failure(StockError.InvalidStockData(e.message ?: "Unknown error"))
            }
        }
}
