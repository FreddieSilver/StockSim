package dev.freddiesilver.stocksim.trading

import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderStatusTest {

    @Test
    fun `PENDING enum value exists`() {
        assertEquals("PENDING", OrderStatus.PENDING.name)
    }

    @Test
    fun `COMPLETED enum value exists`() {
        assertEquals("COMPLETED", OrderStatus.COMPLETED.name)
    }

    @Test
    fun `FAILED enum value exists`() {
        assertEquals("FAILED", OrderStatus.FAILED.name)
    }

    @Test
    fun `OrderStatus has exactly three values`() {
        assertEquals(3, OrderStatus.entries.size)
    }
}
