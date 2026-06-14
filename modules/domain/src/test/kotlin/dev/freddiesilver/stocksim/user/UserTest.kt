package dev.freddiesilver.stocksim.user

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UserTest {

    @Test
    fun `user is created with correct fields`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("5000.00"))
        )
        assertEquals(1L, user.id)
        assertEquals("testuser", user.username.value)
        assertEquals(BigDecimal("5000.00"), user.balance.value)
    }

    @Test
    fun `user with default id is created`() {
        val user = User(
            username = Username("newuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        assertEquals(0L, user.id)
    }

    @Test
    fun `deposit increases balance`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        user.deposit(BigDecimal("50.00"))
        assertEquals(0, BigDecimal("150.00").compareTo(user.balance.value))
    }

    @Test
    fun `deposit with zero amount throws exception`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        val exception = assertFailsWith<IllegalArgumentException> {
            user.deposit(BigDecimal.ZERO)
        }
        assertTrue(exception.message!!.contains("positive"))
    }

    @Test
    fun `deposit with negative amount throws exception`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        assertFailsWith<IllegalArgumentException> {
            user.deposit(BigDecimal("-10.00"))
        }
    }

    @Test
    fun `withdraw decreases balance`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        user.withdraw(BigDecimal("30.00"))
        assertEquals(0, BigDecimal("70.00").compareTo(user.balance.value))
    }

    @Test
    fun `withdraw with insufficient balance throws exception`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        val exception = assertFailsWith<IllegalArgumentException> {
            user.withdraw(BigDecimal("200.00"))
        }
        assertTrue(exception.message!!.contains("Insufficient"))
    }

    @Test
    fun `withdraw with zero amount throws exception`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        assertFailsWith<IllegalArgumentException> {
            user.withdraw(BigDecimal.ZERO)
        }
    }

    @Test
    fun `withdraw with negative amount throws exception`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        assertFailsWith<IllegalArgumentException> {
            user.withdraw(BigDecimal("-10.00"))
        }
    }

    @Test
    fun `withdraw exact balance leaves zero`() {
        val user = User(
            id = 1L,
            username = Username("testuser"),
            balance = Balance(BigDecimal("100.00"))
        )
        user.withdraw(BigDecimal("100.00"))
        assertEquals(0, BigDecimal.ZERO.compareTo(user.balance.value))
    }
}
