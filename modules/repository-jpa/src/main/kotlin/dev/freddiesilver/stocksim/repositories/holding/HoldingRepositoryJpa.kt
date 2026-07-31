package dev.freddiesilver.stocksim.repositories.holding

import dev.freddiesilver.stocksim.HoldingRepository
import dev.freddiesilver.stocksim.entities.HoldingEntity
import dev.freddiesilver.stocksim.entities.toJpaEntity
import dev.freddiesilver.stocksim.repositories.stock.StockJpaRepository
import dev.freddiesilver.stocksim.repositories.user.UserJpaRepository
import dev.freddiesilver.stocksim.trading.holding.Holding
import java.math.BigDecimal

class HoldingRepositoryJpa(
    private val jpa: HoldingJpaRepository,
    private val userJpa: UserJpaRepository,
    private val stockJpa: StockJpaRepository
) : HoldingRepository {

    override fun createHolding(userId: Long, stockId: Long, quantity: BigDecimal): Holding {
        val userProxy = userJpa.getReferenceById(userId)
        val stockProxy = stockJpa.getReferenceById(stockId)

        val entity = HoldingEntity(
            user = userProxy,
            stock = stockProxy,
            quantity = quantity
        )

        val savedEntity = jpa.save(entity)
        return savedEntity.toDomain()
    }

    override fun findByUserIdAndStockId(userId: Long, stockId: Long): Holding? =
        jpa.findByUser_IdAndStock_Id(userId, stockId)?.toDomain()

    override fun findByUserId(userId: Long): List<Holding> =
        jpa.findByUser_Id(userId).map { it.toDomain() }

    override fun findById(id: Long): Holding? =
        jpa.findById(id).orElse(null)?.toDomain()

    override fun findAll(): List<Holding> =
        jpa.findAll().map { it.toDomain() }

    override fun update(entity: Holding) {
        jpa.save(entity.toJpaEntity())
    }

    override fun deleteById(id: Long) = jpa.deleteById(id)
    override fun clear() = jpa.deleteAll()
}
