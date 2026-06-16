package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.trading.stock.Stock
import java.math.BigDecimal

interface StockRepository : Repository<Stock> {
    fun createStock(
        ticker: String,
        company: Company,
        initialPrice: BigDecimal,
    ): Stock

    fun updateAllPrices(stocks: List<Stock>) // Update multiple prices at once

    fun updatePrice(id: Long, newPrice: BigDecimal) // Update the price of a stock by its id
}
