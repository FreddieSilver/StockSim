package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.Stock
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StockRepositoryMemTest {

    private lateinit var repo: StockRepository

    @BeforeTest
    fun setup() {
        repo = StockRepositoryMem()
    }

    private fun createTestCompany(
        ticker: String = "AAPL",
        name: String = "Apple Inc.",
        volatility: Double = 0.02,
        drift: Double = 0.001
    ) = Company(
        id = 0L,
        name = CompanyName(name),
        ticker = Ticker(ticker),
        description = Description("Test company"),
        volatility = volatility,
        drift = drift
    )

    private fun createTestStock(
        ticker: String = "AAPL",
        name: String = "Apple Inc.",
        price: String = "150.00",
        volatility: Double = 0.02,
        drift: Double = 0.001
    ) = repo.createStock(ticker, createTestCompany(ticker, name, volatility, drift), BigDecimal(price))

    @Test
    fun `createStock returns stock with generated id`() {
        val stock = createTestStock()
        assertEquals(1L, stock.id)
        assertEquals("AAPL", stock.company.ticker.value)
        assertEquals("Apple Inc.", stock.company.name.value)
        assertEquals(BigDecimal("150.00"), stock.price.value)
    }

    @Test
    fun `createStock generates sequential ids`() {
        val stock1 = createTestStock(ticker = "AAPL", name = "Apple Inc.")
        val stock2 = createTestStock(ticker = "GOOGL", name = "Alphabet Inc.")
        assertEquals(1L, stock1.id)
        assertEquals(2L, stock2.id)
    }

    @Test
    fun `findById returns existing stock`() {
        val created = createTestStock(ticker = "MSFT", name = "Microsoft Corp.", price = "300.00")
        val found = repo.findById(created.id)
        assertNotNull(found)
        assertEquals("MSFT", found.company.ticker.value)
    }

    @Test
    fun `findById returns null for non-existent id`() {
        assertNull(repo.findById(999L))
    }

    @Test
    fun `findAll returns all stocks`() {
        createTestStock(ticker = "AAPL", name = "Apple Inc.")
        createTestStock(ticker = "GOOGL", name = "Alphabet Inc.")
        assertEquals(2, repo.findAll().size)
    }

    @Test
    fun `findAll returns empty list when no stocks`() {
        assertTrue(repo.findAll().isEmpty())
    }

    @Test
    fun `updatePrice changes stock price`() {
        val stock = createTestStock()
        repo.updatePrice(stock.id, BigDecimal("175.00"))
        val updated = repo.findById(stock.id)
        assertNotNull(updated)
        assertEquals(BigDecimal("175.00"), updated.price.value)
    }

    @Test
    fun `updatePrice for non-existent stock does nothing`() {
        createTestStock()
        repo.updatePrice(999L, BigDecimal("200.00"))
        val stock = repo.findById(1L)
        assertEquals(BigDecimal("150.00"), stock!!.price.value)
    }

    @Test
    fun `updateAllPrices updates multiple stocks`() {
        val stock1 = createTestStock(ticker = "AAPL", name = "Apple Inc.", price = "150.00")
        val stock2 = createTestStock(ticker = "GOOGL", name = "Alphabet Inc.", price = "2800.00")

        val updatedStock1 = stock1.copy(price = Price(BigDecimal("175.00")))
        val updatedStock2 = stock2.copy(price = Price(BigDecimal("2900.00")))

        repo.updateAllPrices(listOf(updatedStock1, updatedStock2))
        assertEquals(BigDecimal("175.00"), repo.findById(stock1.id)!!.price.value)
        assertEquals(BigDecimal("2900.00"), repo.findById(stock2.id)!!.price.value)
    }

    @Test
    fun `update with existing id replaces stock`() {
        val stock = createTestStock(ticker = "AAPL", name = "Apple Inc.")
        val newCompany = createTestCompany(ticker = "MSFT", name = "Microsoft Corp.")
        val replacement = stock.copy(company = newCompany)
        repo.update(replacement)
        val found = repo.findById(stock.id)!!
        assertEquals("MSFT", found.company.ticker.value)
    }

    @Test
    fun `update with id zero creates new stock`() {
        val company = createTestCompany(ticker = "NEW", name = "New Company")
        val stock = Stock(
            company = company,
            price = Price(BigDecimal("10.00"))
        )
        repo.update(stock)
        assertEquals(1, repo.findAll().size)
    }

    @Test
    fun `deleteById removes stock`() {
        val stock = createTestStock()
        repo.deleteById(stock.id)
        assertNull(repo.findById(stock.id))
    }

    @Test
    fun `deleteById does not affect other stocks`() {
        val stock1 = createTestStock(ticker = "AAPL", name = "Apple Inc.")
        val stock2 = createTestStock(ticker = "GOOGL", name = "Alphabet Inc.")
        repo.deleteById(stock1.id)
        assertNull(repo.findById(stock1.id))
        assertNotNull(repo.findById(stock2.id))
    }

    @Test
    fun `clear removes all stocks`() {
        createTestStock(ticker = "AAPL", name = "Apple Inc.")
        createTestStock(ticker = "GOOGL", name = "Alphabet Inc.")
        repo.clear()
        assertTrue(repo.findAll().isEmpty())
    }
}
