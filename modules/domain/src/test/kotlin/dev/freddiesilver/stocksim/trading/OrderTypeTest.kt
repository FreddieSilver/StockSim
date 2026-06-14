package dev.freddiesilver.stocksim.trading

import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderTypeTest {

    @Test
    fun `BUY enum value exists`() {
        assertEquals("BUY", OrderType.BUY.name)
    }

    @Test
    fun `SELL enum value exists`() {
        assertEquals("SELL", OrderType.SELL.name)
    }

    @Test
    fun `OrderType has exactly two values`() {
        assertEquals(2, OrderType.entries.size)
    }
}
