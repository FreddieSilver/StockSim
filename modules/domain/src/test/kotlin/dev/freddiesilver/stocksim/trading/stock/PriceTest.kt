package dev.freddiesilver.stocksim.trading.stock

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PriceTest {
    @Test
    fun `price with positive value is created successfully`() {
        val price = Price(BigDecimal("150.00"))
        assertEquals(BigDecimal("150.00"), price.value)
    }

    @Test
    fun `price with zero value is created successfully`() {
        val price = Price(BigDecimal.ZERO)
        assertEquals(BigDecimal.ZERO, price.value)
    }

    @Test
    fun `negative price throws exception`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Price(BigDecimal("-0.01"))
            }
        assertTrue(exception.message!!.contains("negative"))
    }

    @Test
    fun `price value is accessible via property`() {
        val price = Price(BigDecimal("123.45"))
        assertEquals(BigDecimal("123.45"), price.value)
    }

    @Test
    fun `prices with same value are equal`() {
        val price1 = Price(BigDecimal("150.00"))
        val price2 = Price(BigDecimal("150.00"))
        assertEquals(price1, price2)
    }

    @Test
    fun `prices with different values are not equal`() {
        val price1 = Price(BigDecimal("100.00"))
        val price2 = Price(BigDecimal("200.00"))
        assertNotEquals(price1, price2)
    }

    @Test
    fun `copy creates independent price with same value`() {
        val price1 = Price(BigDecimal("150.00"))
        val price2 = price1.copy()
        assertEquals(price1, price2)
    }

    @Test
    fun `price is immutable - value cannot be changed after creation`() {
        val price = Price(BigDecimal("100.00"))
        // Price has no setter for 'value' and no update() method
        // The only way to get a new price is to create a new instance
        val newPrice = Price(BigDecimal("200.00"))
        assertEquals(BigDecimal("100.00"), price.value)
        assertEquals(BigDecimal("200.00"), newPrice.value)
    }
}
