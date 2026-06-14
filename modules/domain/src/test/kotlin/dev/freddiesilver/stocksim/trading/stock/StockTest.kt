package dev.freddiesilver.stocksim.trading.stock

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class StockTest {

    private fun createTestCompany(
        ticker: String = "AAPL",
        name: String = "Apple Inc.",
        description: String = "Technology company",
        volatility: Double = 0.02,
        drift: Double = 0.001
    ) = Company(
        id = 0L,
        name = CompanyName(name),
        ticker = Ticker(ticker),
        description = Description(description),
        volatility = volatility,
        drift = drift
    )

    @Test
    fun `stock is created with correct fields`() {
        val company = createTestCompany()
        val stock = Stock(
            id = 1L,
            company = company,
            price = Price(BigDecimal("150.00"))
        )
        assertEquals(1L, stock.id)
        assertEquals("AAPL", stock.company.ticker.value)
        assertEquals("Apple Inc.", stock.company.name.value)
        assertEquals(BigDecimal("150.00"), stock.price.value)
    }

    @Test
    fun `stock with default id is created`() {
        val stock = Stock(
            company = createTestCompany(ticker = "GOOGL", name = "Alphabet Inc."),
            price = Price(BigDecimal("2800.00"))
        )
        assertEquals(0L, stock.id)
    }

    @Test
    fun `stock price can be updated by reassignment`() {
        val stock = Stock(
            id = 1L,
            company = createTestCompany(),
            price = Price(BigDecimal("150.00"))
        )
        stock.price = Price(BigDecimal("175.00"))
        assertEquals(BigDecimal("175.00"), stock.price.value)
    }

    @Test
    fun `stock company fields are accessible`() {
        val company = createTestCompany(
            ticker = "TSLA",
            name = "Tesla Inc.",
            description = "Electric vehicle company",
            volatility = 0.05,
            drift = 0.002
        )
        val stock = Stock(id = 1L, company = company, price = Price(BigDecimal("250.00")))
        assertEquals("TSLA", stock.company.ticker.value)
        assertEquals("Tesla Inc.", stock.company.name.value)
        assertEquals("Electric vehicle company", stock.company.description.value)
        assertEquals(0.05, stock.company.volatility)
        assertEquals(0.002, stock.company.drift)
    }
}
