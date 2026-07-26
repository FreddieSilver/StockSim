package dev.freddiesilver.stocksim.user

import dev.freddiesilver.stocksim.UserRepository
import dev.freddiesilver.stocksim.UserRepositoryMem
import dev.freddiesilver.stocksim.user.error.UserError
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class UserServiceTest {
    private lateinit var service: UserService
    private lateinit var userRepo: UserRepository

    @BeforeTest
    fun setup() {
        userRepo = UserRepositoryMem()
        service = UserService(userRepo)
    }

    @Test
    fun `createUser returns User with new user on success`() {
        val result = service.createUser("newuser", "newuser@example.com", "password123")
        assertIs<User>(result)
        assertEquals("newuser", result.username.value)
    }

    @Test
    fun `createUser throws when email already exists`() {
        service.createUser("first", "existing@example.com", "password123")
        assertFailsWith<UserError.UserAlreadyExists> { service.createUser("first", "existing@example.com", "password123") }
    }

    @Test
    fun `createUser throws when username is invalid`() {
        assertFailsWith<IllegalArgumentException> { service.createUser("bad_user!", "bad@example.com", "password123") }
    }

    @Test
    fun `getUserById returns User when user exists`() {
        val user = service.createUser("findme", "findme@example.com", "password123")
        val result = service.getUserById(user.id)
        assertEquals("findme", result.username.value)
    }

    @Test
    fun `getUserById throws when user not found`() {
        assertFailsWith<UserError.UserNotFound> { service.getUserById(999L) }
    }

    @Test
    fun `deposit returns User on success`() {
        val user = service.createUser("testuser", "test@example.com", "password123")
        val result = service.deposit(user.id, BigDecimal("50.00"))
        assertEquals(user.id, result.id)
    }

    @Test
    fun `deposit throws when user not found`() {
        assertFailsWith<UserError.UserNotFound> { service.deposit(999L, BigDecimal("50.00")) }
    }

    @Test
    fun `deposit returns Failure when amount is invalid`() {
        val user = service.createUser("testuser", "test@example.com", "password123")
        assertFailsWith<IllegalArgumentException> { service.deposit(user.id, BigDecimal("-10.00")) }
    }

    @Test
    fun `withdraw returns User on success`() {
        val user = service.createUser("testuser", "test@example.com", "password123")
        service.deposit(user.id, BigDecimal("100.00"))
        val result = service.withdraw(user.id, BigDecimal("50.00"))
        assertIs<User>(result)
        assertEquals(BigDecimal("50.00"), result.balance.value)
    }

    @Test
    fun `withdraw returns Failure when user not found`() {
        assertFailsWith<UserError.UserNotFound> { service.withdraw(999L, BigDecimal("50.00")) }
    }

    @Test
    fun `withdraw throws when insufficient balance`() {
        val user = service.createUser("testuser", "test@example.com", "password123")
        assertFailsWith<IllegalArgumentException> { service.withdraw(user.id, BigDecimal("200.00")) }
    }
}
