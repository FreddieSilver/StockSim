package dev.freddiesilver.stocksim.trading.stock

import dev.freddiesilver.stocksim.company.CompanyName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CompanyNameTest {
    @Test
    fun `company name stores value correctly`() {
        val name = CompanyName("Apple Inc.")
        assertEquals("Apple Inc.", name.value)
    }

    @Test
    fun `company name with special characters is preserved`() {
        val name = CompanyName("Berkshire Hathaway Inc. (Class B)")
        assertEquals("Berkshire Hathaway Inc. (Class B)", name.value)
    }

    @Test
    fun `blank company name throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { CompanyName("") }
        assertTrue(exception.message!!.contains("blank"))
    }

    @Test
    fun `whitespace-only company name throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { CompanyName("   ") }
        assertTrue(exception.message!!.contains("blank"))
    }

    @Test
    fun `company name exceeding 100 characters throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { CompanyName("a".repeat(101)) }
        assertTrue(exception.message!!.contains("100"))
    }

    @Test
    fun `company name at max length 100 is valid`() {
        val name = CompanyName("a".repeat(100))
        assertEquals(100, name.value.length)
    }

    @Test
    fun `toString returns the raw value`() {
        val name = CompanyName("Apple Inc.")
        assertEquals("Apple Inc.", name.toString())
    }
}
