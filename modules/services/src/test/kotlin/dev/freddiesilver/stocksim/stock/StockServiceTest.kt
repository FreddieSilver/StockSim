package dev.freddiesilver.stocksim.stock

import dev.freddiesilver.stocksim.CompanyRepository
import dev.freddiesilver.stocksim.CompanyRepositoryMem
import dev.freddiesilver.stocksim.StockRepository
import dev.freddiesilver.stocksim.StockRepositoryMem
import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.stock.error.StockError
import dev.freddiesilver.stocksim.trading.stock.Stock
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class StockServiceTest {
    private lateinit var service: StockService
    private lateinit var companyRepo: CompanyRepository
    private lateinit var stockRepo: StockRepository

    private lateinit var apple: Company

    @BeforeTest
    fun setup() {
        companyRepo = CompanyRepositoryMem()
        stockRepo = StockRepositoryMem()
        service = StockService(companyRepo, stockRepo)
        apple = companyRepo.createCompany("Apple Inc.", "AAPL", "it's fuckin apple", 0.02, 0.001)
    }

    @Test
    fun `createStock returns Stock with new stock on success`() {
        val result = service.createStock(apple.id, 150.0)
        assertIs<Stock>(result)
        assertEquals("AAPL", result.company.ticker.value)
        assertEquals("Apple Inc.", result.company.name.value)
        assertEquals(BigDecimal("150.0"), result.price.value)
    }

    @Test
    fun `getStockById returns Stock when stock exists`() {
        val stock = service.createStock(apple.id, 150.0)
        val result = service.getStockById(stock.id)
        assertIs<Stock>(result)
        assertEquals("AAPL", result.company.ticker.value)
    }

    @Test
    fun `getStockById throws error when stock not found`() {
        assertFailsWith<StockError.StockNotFound> { service.getStockById(999L) }
    }

    @Test
    fun `updateStockPrice returns Stock with updated stock on success`() {
        val stock = service.createStock(apple.id, 150.0)
        val result = service.updateStockPrice(stock.id, 175.0)
        assertIs<Stock>(result)
        assertEquals(BigDecimal("175.0"), result.price.value)
    }

    @Test
    fun `updateStockPrice throws error when stock not found`() {
        assertFailsWith<StockError.StockNotFound> { service.updateStockPrice(999L, 175.0) }
    }

    @Test
    fun `updateStockPrice throws error when company does not exist`() {
        assertFailsWith<StockError.CompanyNotFound> { service.createStock(56, 150.0) }
    }
}
