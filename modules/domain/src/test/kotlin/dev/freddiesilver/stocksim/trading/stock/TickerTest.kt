package dev.freddiesilver.stocksim.trading.stock

import dev.freddiesilver.stocksim.company.Ticker
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TickerTest {

    @Test
    fun `ticker stores value correctly`() {
        val ticker = Ticker("AAPL")
        assertEquals("AAPL", ticker.value)
    }

    @Test
    fun `ticker with lowercase is preserved`() {
        val ticker = Ticker("aapl")
        assertEquals("aapl", ticker.value)
    }

    @Test
    fun `ticker with numbers is preserved`() {
        val ticker = Ticker("BRK.B")
        assertEquals("BRK.B", ticker.value)
    }


    @Test
    fun `tickers with same value are equal`() {
        val ticker1 = Ticker("AAPL")
        val ticker2 = Ticker("AAPL")
        assertEquals(ticker1, ticker2)
    }

    @Test
    fun `tickers with different values are not equal`() {
        val ticker1 = Ticker("AAPL")
        val ticker2 = Ticker("GOOGL")
        assertNotEquals(ticker1, ticker2)
    }
}
