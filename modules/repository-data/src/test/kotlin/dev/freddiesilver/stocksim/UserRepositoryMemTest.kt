package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.user.Balance
import dev.freddiesilver.stocksim.user.Email
import dev.freddiesilver.stocksim.user.PasswordValidationInfo
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

    private fun createTestUser(
        username: String,
        email: String = "$username@example.com",
    ): User =
        repo.createUser(
            Username(username),
            Email(email),
            PasswordValidationInfo("hashed_$username"),
        )

    @Test
    fun `createUser returns user with generated id`() {
        val user = createTestUser("testuser")
        assertEquals(1L, user.id)
        assertEquals("testuser", user.username.value)
    }

    @Test
    fun `createUser generates sequential ids`() {
        val user1 = createTestUser("user1")
        val user2 = createTestUser("user2")
        val user3 = createTestUser("user3")
        assertEquals(1L, user1.id)
        assertEquals(2L, user2.id)
        assertEquals(3L, user3.id)
    }

    @Test
    fun `findById returns existing user`() {
        val created = createTestUser("findme")
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
    fun `findByEmail returns existing user`() {
        createTestUser("findme", "findme@test.com")
        val found = repo.findByEmail("findme@test.com")
        assertNotNull(found)
        assertEquals("findme", found.username.value)
    }

    @Test
    fun `findByEmail returns null for non-existent email`() {
        val found = repo.findByEmail("nonexistent@test.com")
        assertNull(found)
    }

    @Test
    fun `findAll returns all users`() {
        createTestUser("user1")
        createTestUser("user2")
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
        val user = createTestUser("testuser")
        user.deposit(BigDecimal("50.00"))
        repo.update(user)
        val updated = repo.findById(user.id)
        assertNotNull(updated)
        assertEquals(0, updated.balance.value.compareTo(BigDecimal("50.00")))
    }

    @Test
    fun `update with modified balance persists withdrawal`() {
        val user = createTestUser("testuser")
        user.deposit(BigDecimal("100.00"))
        user.withdraw(BigDecimal("30.00"))
        repo.update(user)
        val updated = repo.findById(user.id)
        assertNotNull(updated)
        assertEquals(0, updated.balance.value.compareTo(BigDecimal("70.00")))
    }

    @Test
    fun `update with existing id replaces user`() {
        val user = createTestUser("original")
        val modifiedUser =
            User(
                id = user.id,
                username = Username("updated"),
                email = Email("updated@test.com"),
                passwordValidationInfo = PasswordValidationInfo("hashed"),
                balance = Balance(BigDecimal("999.00")),
            )
        repo.update(modifiedUser)
        val found = repo.findById(user.id)!!
        assertEquals("updated", found.username.value)
    }

    @Test
    fun `update with id zero creates new user`() {
        val user =
            User(
                username = Username("new"),
                email = Email("new@test.com"),
                passwordValidationInfo = PasswordValidationInfo("hashed"),
                balance = Balance(BigDecimal("100.00")),
            )
        repo.update(user)
        val all = repo.findAll()
        assertEquals(1, all.size)
        assertEquals("new", all[0].username.value)
    }

    @Test
    fun `deleteById removes user`() {
        val user = createTestUser("todelete")
        repo.deleteById(user.id)
        assertNull(repo.findById(user.id))
    }

    @Test
    fun `deleteById does not affect other users`() {
        val user1 = createTestUser("keep")
        val user2 = createTestUser("todelete")
        repo.deleteById(user2.id)
        assertNotNull(repo.findById(user1.id))
        assertNull(repo.findById(user2.id))
    }

    @Test
    fun `clear removes all users`() {
        createTestUser("user1")
        createTestUser("user2")
        repo.clear()
        assertTrue(repo.findAll().isEmpty())
    }
}
