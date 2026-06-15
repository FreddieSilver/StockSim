package dev.freddiesilver.stocksim.mappers

import dev.freddiesilver.stocksim.entities.TradeOrderEntity
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder

object TradeOrderMapper {
    fun toDomain(entity: TradeOrderEntity): TradeOrder = TradeOrder(
        id = entity.id,
        user = UserMapper.toDomain(entity.user),
        stock = StockMapper.toDomain(entity.stock),
        type = entity.type,
        quantity = entity.quantity,
        priceValueAtOrder = entity.priceAtOrder,
        status = entity.status
    )

    fun toEntity(
        domain: TradeOrder
    ): TradeOrderEntity = TradeOrderEntity(
        id = domain.id,
        user = UserMapper.toEntity(domain.user),
        stock = StockMapper.toEntity(domain.stock),
        type = domain.type,
        quantity = domain.quantity,
        priceAtOrder = domain.priceValueAtOrder,
        status = domain.status
    )
}