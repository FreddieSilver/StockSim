package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.trading.holding.Holding
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HoldingRepositoryMemTest {
    private lateinit var repo: HoldingRepository

    @BeforeTest
    fun setup() {
        repo = HoldingRepositoryMem()
    }

    @Test
    fun `createHolding returns holding with generated id`() {
        val holding = repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        assertEquals(1L, holding.id)
        assertEquals(1L, holding.userId)
        assertEquals(10L, holding.stockId)
        assertEquals(100, holding.quantity)
    }

    @Test
    fun `createHolding generates sequential ids`() {
        val h1 = repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        val h2 = repo.createHolding(userId = 2L, stockId = 20L, quantity = 200)
        assertEquals(1L, h1.id)
        assertEquals(2L, h2.id)
    }

    @Test
    fun `findById returns existing holding`() {
        val created = repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        val found = repo.findById(created.id)
        assertNotNull(found)
        assertEquals(100, found.quantity)
    }

    @Test
    fun `findById returns null for non-existent id`() {
        assertNull(repo.findById(999L))
    }

    @Test
    fun `findAll returns all holdings`() {
        repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        repo.createHolding(userId = 2L, stockId = 20L, quantity = 200)
        assertEquals(2, repo.findAll().size)
    }

    @Test
    fun `findAll returns empty list when no holdings`() {
        assertTrue(repo.findAll().isEmpty())
    }

    @Test
    fun `findByUserAndStock returns matching holding`() {
        repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        val found = repo.findByUserAndStock(userId = 1L, stockId = 10L)
        assertNotNull(found)
        assertEquals(100, found.quantity)
    }

    @Test
    fun `findByUserAndStock returns null when no match`() {
        repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        assertNull(repo.findByUserAndStock(userId = 1L, stockId = 99L))
        assertNull(repo.findByUserAndStock(userId = 99L, stockId = 10L))
    }

    @Test
    fun `findByUserId returns only that users holdings`() {
        repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        repo.createHolding(userId = 1L, stockId = 20L, quantity = 200)
        repo.createHolding(userId = 2L, stockId = 30L, quantity = 300)
        val user1Holdings = repo.findByUserId(userId = 1L)
        assertEquals(2, user1Holdings.size)
        assertTrue(user1Holdings.all { it.userId == 1L })
    }

    @Test
    fun `findByUserId returns empty list when user has no holdings`() {
        assertTrue(repo.findByUserId(999L).isEmpty())
    }

    @Test
    fun `update with existing id replaces holding`() {
        val holding = repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        val updated = holding.copy(quantity = 500)
        repo.update(updated)
        val found = repo.findById(holding.id)!!
        assertEquals(500, found.quantity)
    }

    @Test
    fun `update with id zero creates new holding`() {
        val holding = Holding(userId = 1L, stockId = 10L, quantity = 100)
        repo.update(holding)
        assertEquals(1, repo.findAll().size)
    }

    @Test
    fun `deleteById removes holding`() {
        val holding = repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        repo.deleteById(holding.id)
        assertNull(repo.findById(holding.id))
    }

    @Test
    fun `deleteById does not affect other holdings`() {
        val h1 = repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        val h2 = repo.createHolding(userId = 2L, stockId = 20L, quantity = 200)
        repo.deleteById(h1.id)
        assertNull(repo.findById(h1.id))
        assertNotNull(repo.findById(h2.id))
    }

    @Test
    fun `clear removes all holdings`() {
        repo.createHolding(userId = 1L, stockId = 10L, quantity = 100)
        repo.createHolding(userId = 2L, stockId = 20L, quantity = 200)
        repo.clear()
        assertTrue(repo.findAll().isEmpty())
    }
}
