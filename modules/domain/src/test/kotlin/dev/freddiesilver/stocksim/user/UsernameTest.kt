package dev.freddiesilver.stocksim.user

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UsernameTest {
    @Test
    fun `valid username is created successfully`() {
        val username = Username("john_doe")
        assertEquals("john_doe", username.value)
    }

    @Test
    fun `username with letters only is valid`() {
        val username = Username("JohnDoe")
        assertEquals("JohnDoe", username.value)
    }

    @Test
    fun `username with numbers only is valid`() {
        val username = Username("12345")
        assertEquals("12345", username.value)
    }

    @Test
    fun `username with mixed alphanumeric and underscores is valid`() {
        val username = Username("user_123_test")
        assertEquals("user_123_test", username.value)
    }

    @Test
    fun `username at max length 20 is valid`() {
        val username = Username("a".repeat(20))
        assertEquals(20, username.value.length)
    }

    @Test
    fun `blank username throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Username("") }
        assertTrue(exception.message!!.contains("blank"))
    }

    @Test
    fun `whitespace-only username throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Username("   ") }
        assertTrue(exception.message!!.contains("blank"))
    }

    @Test
    fun `username exceeding 20 characters throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Username("a".repeat(21)) }
        assertTrue(exception.message!!.contains("20"))
    }

    @Test
    fun `username with special characters throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Username("user@name") }
        assertTrue(exception.message!!.contains("letters, numbers, and underscores"))
    }

    @Test
    fun `username with spaces throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Username("user name") }
        assertTrue(exception.message!!.contains("letters, numbers, and underscores"))
    }

    @Test
    fun `username with hyphen throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Username("user-name") }
        assertTrue(exception.message!!.contains("letters, numbers, and underscores"))
    }

    @Test
    fun `toString returns the raw value`() {
        val username = Username("test_user")
        assertEquals("test_user", username.toString())
    }
}
