package dev.freddiesilver.stocksim.repositories.holding

import dev.freddiesilver.stocksim.HoldingRepository
import dev.freddiesilver.stocksim.trading.holding.Holding
import dev.freddiesilver.stocksim.entities.HoldingEntity
import dev.freddiesilver.stocksim.mappers.HoldingMapper

class HoldingRepositoryJpa(
    private val jpa: HoldingJpaRepository
) : HoldingRepository {

    override fun createHolding(userId: Long, stockId: Long, quantity: Int): Holding {
        val entity = jpa.save(
            HoldingEntity(
                userId = userId,
                stockId = stockId,
                quantity = quantity
            )
        )
        return HoldingMapper.toDomain(entity)
    }

    override fun findByUserAndStock(userId: Long, stockId: Long): Holding? =
        jpa.findByUserIdAndStockId(userId, stockId)?.let { HoldingMapper.toDomain(it) }

    override fun findByUserId(userId: Long): List<Holding> =
        jpa.findByUserId(userId).map { HoldingMapper.toDomain(it) }

    override fun findById(id: Long): Holding? =
        jpa.findById(id).orElse(null)?.let { HoldingMapper.toDomain(it) }

    override fun findAll(): List<Holding> =
        jpa.findAll().map { HoldingMapper.toDomain(it) }

    override fun update(entity: Holding) {
        jpa.save(HoldingMapper.toEntity(entity))
    }

    override fun deleteById(id: Long) =
        jpa.deleteById(id)

    override fun clear() =
        jpa.deleteAll()
}
