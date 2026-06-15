package dev.freddiesilver.stocksim.stock

import dev.freddiesilver.stocksim.Failure
import dev.freddiesilver.stocksim.Success
import dev.freddiesilver.stocksim.stock.error.StockError
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.transaction.TransactionManagerMem
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class StockServiceTest {

    private lateinit var service: StockService

    @BeforeTest
    fun setup() {
        service = StockService(TransactionManagerMem())
    }

    @Test
    fun `createStock returns Success with new stock on success`() {
        val result = service.createStock("AAPL", "Apple Inc.", 150.0)
        assertIs<Success<Stock>>(result)
        assertEquals("AAPL", result.value.company.ticker.value)
        assertEquals("Apple Inc.", result.value.company.name.value)
        assertEquals(BigDecimal("150.0"), result.value.price.value)
    }

    @Test
    fun `createStock returns Failure when repository throws`() {
        val result = service.createStock("", "Bad Corp", -2.0)
        assertIs<Failure<StockError>>(result)
        assertIs<StockError.InvalidStockData>(result.value)
    }

    @Test
    fun `getStockById returns Success when stock exists`() {
        val created = service.createStock("AAPL", "Apple Inc.", 150.0)
        val stock = (created as Success).value
        val result = service.getStockById(stock.id)
        assertIs<Success<Stock>>(result)
        assertEquals("AAPL", result.value.company.ticker.value)
    }

    @Test
    fun `getStockById returns Failure when stock not found`() {
        val result = service.getStockById(999L)
        assertIs<Failure<StockError>>(result)
        assertIs<StockError.StockNotFound>(result.value)
    }

    @Test
    fun `updateStockPrice returns Success with updated stock on success`() {
        val created = service.createStock("AAPL", "Apple Inc.", 150.0)
        val stock = (created as Success).value
        val result = service.updateStockPrice(stock.id, 175.0)
        assertIs<Success<Stock>>(result)
        assertEquals(BigDecimal("175.0"), result.value.price.value)
    }

    @Test
    fun `updateStockPrice returns Failure when stock not found`() {
        val result = service.updateStockPrice(999L, 175.0)
        assertIs<Failure<StockError>>(result)
        assertIs<StockError.StockNotFound>(result.value)
    }

    @Test
    fun `updateStockPrice returns Failure when price is invalid`() {
        val created = service.createStock("AAPL", "Apple Inc.", 150.0)
        val stock = (created as Success).value
        val result = service.updateStockPrice(stock.id, -10.0)
        assertIs<Failure<StockError>>(result)
        assertIs<StockError.InvalidStockData>(result.value)
    }
}
