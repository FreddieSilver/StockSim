package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.Stock
import java.math.BigDecimal

class StockRepositoryMem : StockRepository {
    private val stocks = mutableListOf<Stock>()
    private var nextId = 1L

    override fun createStock(
        ticker: String,
        company: Company,
        initialPrice: BigDecimal,
    ): Stock =
        Stock(
            id = nextId++,
            company = company,
            price = Price(initialPrice),
        ).also { stocks.add(it) }

    override fun findById(id: Long): Stock? = stocks.firstOrNull { it.id == id }

    override fun findAll(): List<Stock> = stocks.toList()

    override fun update(entity: Stock) {
        if (entity.id == 0L) {
            val newStock =
                Stock(
                    id = nextId++,
                    entity.company,
                    price = entity.price,
                )
            stocks.add(newStock)
        } else {
            stocks.removeIf { it.id == entity.id }
            stocks.add(entity)
        }
    }

    override fun deleteById(id: Long) {
        stocks.removeIf { it.id == id }
    }

    override fun clear() = stocks.clear()

    override fun updateAllPrices(stocks: List<Stock>) =
        stocks.forEach { stock ->
            this.stocks.find { it.id == stock.id }?.let { existing ->
                existing.price = Price(stock.price.value)
            }
        }

    override fun updatePrice(
        id: Long,
        newPrice: BigDecimal,
    ) {
        stocks.find { it.id == id }?.let { existing ->
            existing.price = Price(newPrice)
        }
    }
}
