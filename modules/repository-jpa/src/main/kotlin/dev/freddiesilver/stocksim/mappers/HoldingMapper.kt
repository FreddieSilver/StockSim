package dev.freddiesilver.stocksim.mappers

import dev.freddiesilver.stocksim.entities.HoldingEntity
import dev.freddiesilver.stocksim.trading.holding.Holding

object HoldingMapper {
    fun toDomain(entity: HoldingEntity): Holding = Holding(
        id = entity.id,
        userId = entity.userId,
        stockId = entity.stockId,
        quantity = entity.quantity
    )

    fun toEntity(domain: Holding): HoldingEntity = HoldingEntity(
        id = domain.id,
        userId = domain.userId,
        stockId = domain.stockId,
        quantity = domain.quantity
    )
}