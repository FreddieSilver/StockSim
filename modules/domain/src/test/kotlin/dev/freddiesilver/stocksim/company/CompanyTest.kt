package dev.freddiesilver.stocksim.company

import kotlin.test.Test
import kotlin.test.assertEquals

class CompanyTest {
    @Test
    fun `company is created with correct fields`() {
        val company =
            Company(
                id = 1L,
                name = CompanyName("Apple Inc."),
                ticker = Ticker("AAPL"),
                description = Description("Technology company"),
                volatility = 0.02,
                drift = 0.001,
            )
        assertEquals(1L, company.id)
        assertEquals("Apple Inc.", company.name.value)
        assertEquals("AAPL", company.ticker.value)
        assertEquals("Technology company", company.description.value)
        assertEquals(0.02, company.volatility)
        assertEquals(0.001, company.drift)
    }

    @Test
    fun `company with zero volatility is valid`() {
        val company =
            Company(
                id = 1L,
                name = CompanyName("Stable Corp"),
                ticker = Ticker("STBL"),
                description = Description("A stable company"),
                volatility = 0.0,
                drift = 0.0,
            )
        assertEquals(0.0, company.volatility)
        assertEquals(0.0, company.drift)
    }

    @Test
    fun `companies with same fields are equal`() {
        val c1 =
            Company(
                id = 1L,
                name = CompanyName("Apple Inc."),
                ticker = Ticker("AAPL"),
                description = Description("Tech"),
                volatility = 0.02,
                drift = 0.001,
            )
        val c2 =
            Company(
                id = 1L,
                name = CompanyName("Apple Inc."),
                ticker = Ticker("AAPL"),
                description = Description("Tech"),
                volatility = 0.02,
                drift = 0.001,
            )
        assertEquals(c1, c2)
    }
}
