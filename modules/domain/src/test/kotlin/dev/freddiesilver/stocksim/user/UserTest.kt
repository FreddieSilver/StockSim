package dev.freddiesilver.stocksim.user

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UserTest {
    private fun createTestUser(
        id: Long = 0,
        username: String = "testuser",
        email: String = "test@example.com",
        balance: BigDecimal = BigDecimal("100.00"),
    ) = User(
        id = id,
        username = Username(username),
        email = Email(email),
        passwordValidationInfo = PasswordValidationInfo("hashed"),
        balance = Balance(balance),
    )

    @Test
    fun `user is created with correct fields`() {
        val user =
            createTestUser(
                id = 1L,
                username = "testuser",
                email = "test@example.com",
                balance = BigDecimal("5000.00"),
            )
        assertEquals(1L, user.id)
        assertEquals("testuser", user.username.value)
        assertEquals("test@example.com", user.email.value)
        assertEquals(BigDecimal("5000.00"), user.balance.value)
    }

    @Test
    fun `user with default id is created`() {
        val user = createTestUser(username = "newuser", balance = BigDecimal("100.00"))
        assertEquals(0L, user.id)
    }

    @Test
    fun `deposit increases balance`() {
        val user = createTestUser()
        user.deposit(BigDecimal("50.00"))
        assertEquals(0, BigDecimal("150.00").compareTo(user.balance.value))
    }

    @Test
    fun `deposit with zero amount throws exception`() {
        val user = createTestUser()
        val exception =
            assertFailsWith<IllegalArgumentException> {
                user.deposit(BigDecimal.ZERO)
            }
        assertTrue(exception.message!!.contains("positive"))
    }

    @Test
    fun `deposit with negative amount throws exception`() {
        val user = createTestUser()
        assertFailsWith<IllegalArgumentException> {
            user.deposit(BigDecimal("-10.00"))
        }
    }

    @Test
    fun `withdraw decreases balance`() {
        val user = createTestUser()
        user.withdraw(BigDecimal("30.00"))
        assertEquals(0, BigDecimal("70.00").compareTo(user.balance.value))
    }

    @Test
    fun `withdraw with insufficient balance throws exception`() {
        val user = createTestUser()
        val exception =
            assertFailsWith<IllegalArgumentException> {
                user.withdraw(BigDecimal("200.00"))
            }
        assertTrue(exception.message!!.contains("Insufficient"))
    }

    @Test
    fun `withdraw with zero amount throws exception`() {
        val user = createTestUser()
        assertFailsWith<IllegalArgumentException> {
            user.withdraw(BigDecimal.ZERO)
        }
    }

    @Test
    fun `withdraw with negative amount throws exception`() {
        val user = createTestUser()
        assertFailsWith<IllegalArgumentException> {
            user.withdraw(BigDecimal("-10.00"))
        }
    }

    @Test
    fun `withdraw exact balance leaves zero`() {
        val user = createTestUser()
        user.withdraw(BigDecimal("100.00"))
        assertEquals(0, BigDecimal.ZERO.compareTo(user.balance.value))
    }
}
