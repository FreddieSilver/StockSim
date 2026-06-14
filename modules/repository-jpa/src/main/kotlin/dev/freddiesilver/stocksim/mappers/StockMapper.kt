package dev.freddiesilver.stocksim.mappers

import dev.freddiesilver.stocksim.entities.StockEntity
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.Stock

object StockMapper {
    fun toDomain(stockEntity: StockEntity): Stock {
        return Stock(
            id = stockEntity.id,
            company = CompanyMapper.toDomain(stockEntity.company),
            price = Price(stockEntity.price)
        )
    }
}