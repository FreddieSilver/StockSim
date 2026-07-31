package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.holding.Holding
import java.math.BigDecimal

class HoldingRepositoryMem(
    private val userRepo: UserRepository,
    private val stockRepo: StockRepository
) : HoldingRepository {
    private val holdings = mutableListOf<Holding>()

    override fun createHolding(
        userId: Long,
        stockId: Long,
        quantity: BigDecimal,
    ): Holding {
        val user = userRepo.findById(userId)
            ?: throw IllegalArgumentException("User not found with ID: $userId")
        val stock = stockRepo.findById(stockId)
            ?: throw IllegalArgumentException("Stock not found with ID: $stockId")
        val newHolding =
            Holding(
                id = holdings.size + 1L,
                user = user,
                stock = stock,
                quantity = quantity,
            )
        holdings.add(newHolding)
        return newHolding
    }

    override fun findByUserIdAndStockId(
        userId: Long,
        stockId: Long,
    ): Holding?{
        val user = userRepo.findById(userId)
            ?: return null
        val stock = stockRepo.findById(stockId)
            ?: return null
        return holdings.firstOrNull { it.user == user && it.stock == stock }
    }

    override fun findByUserId(userId: Long): List<Holding> {
        val user = userRepo.findById(userId)
            ?: return emptyList()
        return holdings.filter { it.user == user }
    }

    override fun findById(id: Long): Holding? = holdings.firstOrNull { it.id == id }

    override fun findAll(): List<Holding> = holdings.toList()

    override fun update(entity: Holding) {
        if (entity.id == 0L) {
            val newHolding = entity.copy(id = holdings.size + 1L)
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
