package dev.freddiesilver.stocksim.user

import dev.freddiesilver.stocksim.Failure
import dev.freddiesilver.stocksim.Success
import dev.freddiesilver.stocksim.transaction.TransactionManagerMem
import dev.freddiesilver.stocksim.user.error.UserError
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UserServiceTest {
    private lateinit var service: UserService

    @BeforeTest
    fun setup() {
        service = UserService(TransactionManagerMem())
    }

    @Test
    fun `createUser returns Success with new user on success`() {
        val result = service.createUser("newuser", "newuser@example.com", "password123")
        assertIs<Success<User>>(result)
        assertEquals("newuser", result.value.username.value)
    }

    @Test
    fun `createUser returns Failure when email already exists`() {
        service.createUser("first", "existing@example.com", "password123")
        val result = service.createUser("second", "existing@example.com", "password456")
        assertIs<Failure<UserError>>(result)
        assertIs<UserError.UserAlreadyExists>(result.value)
    }

    @Test
    fun `createUser returns Failure when username is invalid`() {
        val result = service.createUser("bad_user!", "bad@example.com", "password123")
        assertIs<Failure<UserError>>(result)
        assertIs<UserError.InvalidUserData>(result.value)
    }

    @Test
    fun `getUserById returns Success when user exists`() {
        val created = service.createUser("findme", "findme@example.com", "password123")
        val user = (created as Success).value
        val result = service.getUserById(user.id)
        assertIs<Success<User>>(result)
        assertEquals("findme", result.value.username.value)
    }

    @Test
    fun `getUserById returns Failure when user not found`() {
        val result = service.getUserById(999L)
        assertIs<Failure<UserError>>(result)
        assertIs<UserError.UserNotFound>(result.value)
    }

    @Test
    fun `deposit returns Success on success`() {
        val created = service.createUser("testuser", "test@example.com", "password123")
        val user = (created as Success).value
        val result = service.deposit(user.id, BigDecimal("50.00"))
        assertIs<Success<User>>(result)
        assertEquals(user.id, result.value.id)
    }

    @Test
    fun `deposit returns Failure when user not found`() {
        val result = service.deposit(999L, BigDecimal("50.00"))
        assertIs<Failure<UserError>>(result)
        assertIs<UserError.UserNotFound>(result.value)
    }

    @Test
    fun `deposit returns Failure when amount is invalid`() {
        val created = service.createUser("testuser", "test@example.com", "password123")
        val user = (created as Success).value
        val result = service.deposit(user.id, BigDecimal("-10.00"))
        assertIs<Failure<UserError>>(result)
        assertIs<UserError.InvalidDepositAmount>(result.value)
    }

    @Test
    fun `withdraw returns Success on success`() {
        val created = service.createUser("testuser", "test@example.com", "password123")
        val user = (created as Success).value
        service.deposit(user.id, BigDecimal("100.00"))
        val result = service.withdraw(user.id, BigDecimal("50.00"))
        assertIs<Success<User>>(result)
    }

    @Test
    fun `withdraw returns Failure when user not found`() {
        val result = service.withdraw(999L, BigDecimal("50.00"))
        assertIs<Failure<UserError>>(result)
        assertIs<UserError.UserNotFound>(result.value)
    }

    @Test
    fun `withdraw returns Failure when insufficient balance`() {
        val created = service.createUser("testuser", "test@example.com", "password123")
        val user = (created as Success).value
        val result = service.withdraw(user.id, BigDecimal("200.00"))
        assertIs<Failure<UserError>>(result)
        assertIs<UserError.InsufficientBalance>(result.value)
    }
}
