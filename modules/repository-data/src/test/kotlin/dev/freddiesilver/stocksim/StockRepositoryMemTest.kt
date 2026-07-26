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
    private lateinit var stockRepo: StockRepository

    @BeforeTest
    fun setup() {
        stockRepo = StockRepositoryMem()
    }

    private fun createTestCompany(
        ticker: String = "AAPL",
        name: String = "Apple Inc.",
        volatility: Double = 0.02,
        drift: Double = 0.001,
    ) = Company(
        id = 1L,
        ticker = Ticker(ticker),
        name = CompanyName(name),
        description = Description("Test company"),
        volatility = volatility,
        drift = drift,
    )

    private fun createTestStock(price: String = "150.00") = stockRepo.createStock(createTestCompany(), BigDecimal(price))

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
        val stock1 = createTestStock()
        val stock2 = createTestStock()
        assertEquals(1L, stock1.id)
        assertEquals(2L, stock2.id)
    }

    @Test
    fun `findById returns existing stock`() {
        val created = createTestStock(price = "300.00")
        val found = stockRepo.findById(created.id)
        assertNotNull(found)
        assertEquals("AAPL", found.company.ticker.value)
    }

    @Test
    fun `findById returns null for non-existent id`() {
        assertNull(stockRepo.findById(999L))
    }

    @Test
    fun `findAll returns all stocks`() {
        createTestStock()
        createTestStock()
        assertEquals(2, stockRepo.findAll().size)
    }

    @Test
    fun `findAll returns empty list when no stocks`() {
        assertTrue(stockRepo.findAll().isEmpty())
    }

    @Test
    fun `updatePrice changes stock price`() {
        val stock = createTestStock()
        stockRepo.updatePrice(stock.id, BigDecimal("175.00"))
        val updated = stockRepo.findById(stock.id)
        assertNotNull(updated)
        assertEquals(BigDecimal("175.00"), updated.price.value)
    }

    @Test
    fun `updatePrice for non-existent stock does nothing`() {
        createTestStock()
        stockRepo.updatePrice(999L, BigDecimal("200.00"))
        val stock = stockRepo.findById(1L)
        assertEquals(BigDecimal("150.00"), stock!!.price.value)
    }

    @Test
    fun `updateAllPrices updates multiple stocks`() {
        val stock1 = createTestStock(price = "150.00")
        val stock2 = createTestStock(price = "2800.00")

        val updatedStock1 = stock1.copy(price = Price(BigDecimal("175.00")))
        val updatedStock2 = stock2.copy(price = Price(BigDecimal("2900.00")))

        stockRepo.updateAllPrices(listOf(updatedStock1, updatedStock2))
        assertEquals(BigDecimal("175.00"), stockRepo.findById(stock1.id)!!.price.value)
        assertEquals(BigDecimal("2900.00"), stockRepo.findById(stock2.id)!!.price.value)
    }

    @Test
    fun `update with existing id replaces stock`() {
        val stock = createTestStock()
        val newCompany = createTestCompany(ticker = "MSFT", name = "Microsoft Corp.")
        val replacement = stock.copy(company = newCompany)
        stockRepo.update(replacement)
        val found = stockRepo.findById(stock.id)!!
        assertEquals("MSFT", found.company.ticker.value)
    }

    @Test
    fun `update with id zero creates new stock`() {
        val company = createTestCompany(ticker = "NEW", name = "New Company")
        val stock =
            Stock(
                company = company,
                price = Price(BigDecimal("10.00")),
            )
        stockRepo.update(stock)
        assertEquals(1, stockRepo.findAll().size)
    }

    @Test
    fun `deleteById removes stock`() {
        val stock = createTestStock()
        stockRepo.deleteById(stock.id)
        assertNull(stockRepo.findById(stock.id))
    }

    @Test
    fun `deleteById does not affect other stocks`() {
        val stock1 = createTestStock()
        val stock2 = createTestStock()
        stockRepo.deleteById(stock1.id)
        assertNull(stockRepo.findById(stock1.id))
        assertNotNull(stockRepo.findById(stock2.id))
    }

    @Test
    fun `clear removes all stocks`() {
        createTestStock()
        createTestStock()
        stockRepo.clear()
        assertTrue(stockRepo.findAll().isEmpty())
    }
}
