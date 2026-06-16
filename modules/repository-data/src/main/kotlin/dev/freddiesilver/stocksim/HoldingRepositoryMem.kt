package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.holding.Holding

class HoldingRepositoryMem : HoldingRepository {
    private val holdings = mutableListOf<Holding>()

    override fun createHolding(
        userId: Long,
        stockId: Long,
        quantity: Int,
    ): Holding {
        val newHolding =
            Holding(
                id = holdings.size + 1L,
                userId = userId,
                stockId = stockId,
                quantity = quantity,
            )
        holdings.add(newHolding)
        return newHolding
    }

    override fun findByUserAndStock(
        userId: Long,
        stockId: Long,
    ): Holding? = holdings.firstOrNull { it.userId == userId && it.stockId == stockId }

    override fun findByUserId(userId: Long): List<Holding> = holdings.filter { it.userId == userId }

    override fun findById(id: Long): Holding? = holdings.firstOrNull { it.id == id }

    override fun findAll(): List<Holding> = holdings.toList()

    override fun update(entity: Holding) {
        if (entity.id == 0L) {
            val newHolding =
                Holding(
                    id = holdings.size + 1L,
                    userId = entity.userId,
                    stockId = entity.stockId,
                    quantity = entity.quantity,
                )
            holdings.add(newHolding)
        } else {
            holdings.removeIf { it.id == entity.id }
            holdings.add(entity)
        }
    }

    override fun deleteById(id: Long) {
        holdings.removeIf { it.id == id }
    }

    override fun clear() = holdings.clear()
}
