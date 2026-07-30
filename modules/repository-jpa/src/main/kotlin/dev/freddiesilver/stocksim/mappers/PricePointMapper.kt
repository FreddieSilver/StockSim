package dev.freddiesilver.stocksim.mappers

import dev.freddiesilver.stocksim.entities.PricePointEntity
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.PricePoint

object PricePointMapper {
    fun toDomain(entity: PricePointEntity): PricePoint =
        PricePoint(
            id = entity.id,
            stockId = entity.stockId,
            price = Price(entity.price),
            timestamp = entity.timestamp
            )

    fun toEntity(domain: PricePoint): PricePointEntity =
        PricePointEntity(
            id = domain.id,
            stockId = domain.stockId,
            price = domain.price.value,
            timestamp = domain.timestamp
        )
}