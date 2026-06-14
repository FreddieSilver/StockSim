package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepositoryMemTest {

    private lateinit var repo: UserRepository

    @BeforeTest
    fun setup() {
        repo = UserRepositoryMem()
    }

    @Test
    fun `createUser returns user with generated id`() {
        val user = repo.createUser("testuser", 1000.0.toBigDecimal())
        assertEquals(1L, user.id)
        assertEquals("testuser", user.username.value)
    }

    @Test
    fun `createUser generates sequential ids`() {
        val user1 = repo.createUser("user1", 100.0.toBigDecimal())
        val user2 = repo.createUser("user2", 200.0.toBigDecimal())
        val user3 = repo.createUser("user3", 300.0.toBigDecimal())
        assertEquals(1L, user1.id)
        assertEquals(2L, user2.id)
        assertEquals(3L, user3.id)
    }

    @Test
    fun `findById returns existing user`() {
        val created = repo.createUser("findme", 500.0.toBigDecimal())
        val found = repo.findById(created.id)
        assertNotNull(found)
        assertEquals("findme", found.username.value)
    }

    @Test
    fun `findById returns null for non-existent id`() {
        val found = repo.findById(999L)
        assertNull(found)
    }

    @Test
    fun `findByUsername returns existing user`() {
        repo.createUser("findme", 500.0.toBigDecimal())
        val found = repo.findByUsername("findme")
        assertNotNull(found)
        assertEquals("findme", found.username.value)
    }

    @Test
    fun `findByUsername returns null for non-existent username`() {
        val found = repo.findByUsername("nonexistent")
        assertNull(found)
    }

    @Test
    fun `findAll returns all users`() {
        repo.createUser("user1", 100.0.toBigDecimal())
        repo.createUser("user2", 200.0.toBigDecimal())
        val all = repo.findAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `findAll returns empty list when no users`() {
        val all = repo.findAll()
        assertTrue(all.isEmpty())
    }

    @Test
    fun `update with modified balance persists deposit`() {
        val user = repo.createUser("testuser", 100.0.toBigDecimal())
        user.deposit(BigDecimal("50.00"))
        repo.update(user)
        val updated = repo.findById(user.id)
        assertNotNull(updated)
        assertEquals(0, updated.balance.value.compareTo(BigDecimal("150.00")))
    }

    @Test
    fun `update with modified balance persists withdrawal`() {
        val user = repo.createUser("testuser", 100.0.toBigDecimal())
        user.withdraw(BigDecimal("30.00"))
        repo.update(user)
        val updated = repo.findById(user.id)
        assertNotNull(updated)
        assertEquals(0, updated.balance.value.compareTo(BigDecimal("70.00")))
    }

    @Test
    fun `update with existing id replaces user`() {
        val user = repo.createUser("original", 100.0.toBigDecimal())
        val modifiedUser = User(
            id = user.id,
            username = Username("updated"),
            balance = BigDecimal("999.00").let { dev.freddiesilver.stocksim.user.Balance(it) }
        )
        repo.update(modifiedUser)
        val found = repo.findById(user.id)!!
        assertEquals("updated", found.username.value)
    }

    @Test
    fun `update with id zero creates new user`() {
        val user = User(
            username = Username("new"),
            balance = dev.freddiesilver.stocksim.user.Balance(BigDecimal("100.00"))
        )
        repo.update(user)
        val all = repo.findAll()
        assertEquals(1, all.size)
        assertEquals("new", all[0].username.value)
    }

    @Test
    fun `deleteById removes user`() {
        val user = repo.createUser("todelete", 100.0.toBigDecimal())
        repo.deleteById(user.id)
        assertNull(repo.findById(user.id))
    }

    @Test
    fun `deleteById does not affect other users`() {
        val user1 = repo.createUser("keep", 100.0.toBigDecimal())
        val user2 = repo.createUser("todelete", 200.0.toBigDecimal())
        repo.deleteById(user2.id)
        assertNotNull(repo.findById(user1.id))
        assertNull(repo.findById(user2.id))
    }

    @Test
    fun `clear removes all users`() {
        repo.createUser("user1", 100.0.toBigDecimal())
        repo.createUser("user2", 200.0.toBigDecimal())
        repo.clear()
        assertTrue(repo.findAll().isEmpty())
    }
}
