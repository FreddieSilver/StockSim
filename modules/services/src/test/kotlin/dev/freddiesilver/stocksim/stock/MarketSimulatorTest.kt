package dev.freddiesilver.stocksim.stock

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.transaction.TransactionManagerMem
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MarketSimulatorTest {

    private lateinit var simulator: MarketSimulator
    private lateinit var trxManager: TransactionManagerMem

    @BeforeTest
    fun setup() {
        trxManager = TransactionManagerMem()
    }

    private fun createCompany(
        ticker: String,
        name: String,
        volatility: Double,
        drift: Double = 0.0
    ) = Company(
        id = 0L,
        name = CompanyName(name),
        ticker = Ticker(ticker),
        description = Description("Test company"),
        volatility = volatility,
        drift = drift
    )

    private fun createStock(
        ticker: String,
        name: String,
        price: String,
        volatility: Double,
        drift: Double = 0.0
    ): Stock {
        val company = createCompany(ticker, name, volatility, drift)
        return trxManager.run { stockRepo.createStock(ticker, company, BigDecimal(price)) }
    }

    @Test
    fun `simulateStep changes prices with high volatility`() {
        createStock("AAPL", "Apple Inc.", "150.00", volatility = 0.5)
        simulator = MarketSimulator(trxManager, Random(42))
        val updated = simulator.simulateStep()
        assertEquals(1, updated.size)
        assertNotEquals(BigDecimal("150.00"), updated[0].price.value)
    }

    @Test
    fun `zero volatility and drift means no price change`() {
        createStock("STBL", "Stable Corp", "100.00", volatility = 0.0, drift = 0.0)
        simulator = MarketSimulator(trxManager, Random(42))
        val updated = simulator.simulateStep()
        assertEquals(BigDecimal("100.00"), updated[0].price.value)
    }

    @Test
    fun `prices never go below 0_01`() {
        createStock("PENNY", "Penny Stock", "0.02", volatility = 0.99)
        for (seed in 1..50) {
            simulator = MarketSimulator(trxManager, Random(seed))
            simulator.simulateStep()
        }
        val stock = trxManager.run { stockRepo.findAll().first() }
        assertTrue(stock.price.value >= BigDecimal("0.01"))
    }

    @Test
    fun `multiple steps keep prices valid`() {
        createStock("AAPL", "Apple Inc.", "150.00", volatility = 0.1, drift = 0.0)
        simulator = MarketSimulator(trxManager, Random(123))
        repeat(10) { simulator.simulateStep() }
        val stock = trxManager.run { stockRepo.findAll().first() }
        assertTrue(stock.price.value >= BigDecimal("0.01"))
        assertEquals(2, stock.price.value.scale())
    }

    @Test
    fun `step on empty repo returns empty list`() {
        simulator = MarketSimulator(trxManager, Random(42))
        val result = simulator.simulateStep()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `prices are rounded to 2 decimal places`() {
        createStock("AAPL", "Apple Inc.", "150.00", volatility = 0.3333)
        simulator = MarketSimulator(trxManager, Random(42))
        simulator.simulateStep()
        val stock = trxManager.run { stockRepo.findAll().first() }
        assertEquals(2, stock.price.value.scale())
    }

    @Test
    fun `high-volatility stock moves more than low-volatility stock`() {
        createStock("STBL", "Stable Corp", "100.00", volatility = 0.01, drift = 0.0)
        createStock("RISKY", "Risky Corp", "100.00", volatility = 0.50, drift = 0.0)
        simulator = MarketSimulator(trxManager, Random(42))
        simulator.simulateStep()
        val stocks = trxManager.run { stockRepo.findAll() }
        val stablePrice = stocks.first { it.company.ticker.value == "STBL" }.price.value
        val riskyPrice = stocks.first { it.company.ticker.value == "RISKY" }.price.value
        val stableDiff = (BigDecimal("100.00") - stablePrice).abs()
        val riskyDiff = (BigDecimal("100.00") - riskyPrice).abs()
        assertTrue(riskyDiff >= stableDiff)
    }

    @Test
    fun `positive drift trends upward over many steps`() {
        createStock("GROW", "Growth Corp", "100.00", volatility = 0.01, drift = 0.05)
        simulator = MarketSimulator(trxManager, Random(42))
        repeat(100) { simulator.simulateStep() }
        val stock = trxManager.run { stockRepo.findAll().first() }
        assertTrue(stock.price.value > BigDecimal("100.00"),
            "Expected price > 100.00 after 100 steps with 5% drift, got ${stock.price.value}")
    }

    @Test
    fun `simulateStep updates all stocks`() {
        createStock("AAPL", "Apple Inc.", "150.00", volatility = 0.1)
        createStock("GOOGL", "Alphabet Inc.", "2800.00", volatility = 0.1)
        createStock("MSFT", "Microsoft Corp.", "300.00", volatility = 0.1)
        simulator = MarketSimulator(trxManager, Random(42))
        val updated = simulator.simulateStep()
        assertEquals(3, updated.size)
    }
}
