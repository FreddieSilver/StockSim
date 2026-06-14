package dev.freddiesilver.stocksim.company

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DescriptionTest {

    @Test
    fun `description stores value correctly`() {
        val desc = Description("A technology company")
        assertEquals("A technology company", desc.value)
    }

    @Test
    fun `blank description throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Description("") }
        assertTrue(exception.message!!.contains("blank"))
    }

    @Test
    fun `whitespace-only description throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Description("   ") }
        assertTrue(exception.message!!.contains("blank"))
    }

    @Test
    fun `description exceeding 1000 characters throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> { Description("a".repeat(1001)) }
        assertTrue(exception.message!!.contains("1000"))
    }

    @Test
    fun `description at max length 1000 is valid`() {
        val desc = Description("a".repeat(1000))
        assertEquals(1000, desc.value.length)
    }

    @Test
    fun `toString returns the raw value`() {
        val desc = Description("Tech company")
        assertEquals("Tech company", desc.toString())
    }
}
