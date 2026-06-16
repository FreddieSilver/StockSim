package dev.freddiesilver.stocksim.tradeorder

import dev.freddiesilver.stocksim.Failure
import dev.freddiesilver.stocksim.Success
import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import dev.freddiesilver.stocksim.tradeorder.error.TradeOrderError
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.transaction.TransactionManager
import dev.freddiesilver.stocksim.transaction.TransactionManagerMem
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TradeOrderServiceTest {
    private lateinit var service: TradeOrderService
    private lateinit var trxManager: TransactionManager

    @BeforeTest
    fun setup() {
        trxManager = TransactionManagerMem()
        service = TradeOrderService(trxManager)
    }

    private fun createTestUser(): Long =
        trxManager.run {
            val user =
                userRepo.createUser(
                    dev.freddiesilver.stocksim.user.Username("testuser"),
                    dev.freddiesilver.stocksim.user.Email("testuser@example.com"),
                    dev.freddiesilver.stocksim.user.PasswordValidationInfo("hashed_pw"),
                )
            user.deposit(BigDecimal("10000.00"))
            userRepo.update(user)
            user.id
        }

    private fun createTestCompany() =
        Company(
            id = 0L,
            name = CompanyName("Apple Inc."),
            ticker = Ticker("AAPL"),
            description = Description("Technology company"),
            volatility = 0.02,
            drift = 0.001,
        )

    private fun createTestStock(): Long =
        trxManager.run {
            val stock = stockRepo.createStock("AAPL", createTestCompany(), BigDecimal("150.00"))
            stock.id
        }

    private fun getTestStock(stockId: Long) = trxManager.run { stockRepo.findById(stockId)!! }

    // --- Validation tests ---

    @Test
    fun `placeOrder returns Failure when quantity is zero`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val result = service.placeOrder(userId, stockId, OrderType.BUY, 0, Price(BigDecimal("1500.00")))
        assertIs<Failure<TradeOrderError>>(result)
        assertIs<TradeOrderError.InvalidOrderDetails>(result.value)
    }

    @Test
    fun `placeOrder returns Failure when quantity is negative`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val result = service.placeOrder(userId, stockId, OrderType.BUY, -5, Price(BigDecimal("1500.00")))
        assertIs<Failure<TradeOrderError>>(result)
        assertIs<TradeOrderError.InvalidOrderDetails>(result.value)
    }

    @Test
    fun `placeOrder returns Failure when user is not found`() {
        val stockId = createTestStock()
        val result = service.placeOrder(999L, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        assertIs<Failure<TradeOrderError>>(result)
        assertIs<TradeOrderError.UserNotFound>(result.value)
    }

    @Test
    fun `placeOrder returns Failure when stock is not found`() {
        val userId = createTestUser()
        val result = service.placeOrder(userId, 999L, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        assertIs<Failure<TradeOrderError>>(result)
        assertIs<TradeOrderError.StockNotFound>(result.value)
    }

    // --- BUY order tests ---

    @Test
    fun `BUY order succeeds with sufficient balance`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val result = service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        assertIs<Success<TradeOrder>>(result)
        assertEquals(10, result.value.quantity)
        assertEquals(OrderType.BUY, result.value.type)
    }

    @Test
    fun `BUY with quantity of one succeeds`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val result = service.placeOrder(userId, stockId, OrderType.BUY, 1, Price(BigDecimal("150.00")))
        assertIs<Success<TradeOrder>>(result)
        assertEquals(1, result.value.quantity)
    }

    @Test
    fun `BUY deducts correct amount from balance`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        trxManager.run {
            val user = userRepo.findById(userId)!!
            assertEquals(0, user.balance.value.compareTo(BigDecimal("8500.00")))
        }
    }

    @Test
    fun `BUY adds holding for purchased stock`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        trxManager.run {
            val holding = holdingRepo.findByUserAndStock(userId, stockId)
            assertNotNull(holding)
            assertEquals(10, holding.quantity)
        }
    }

    @Test
    fun `BUY accumulates quantity when holding already exists`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        service.placeOrder(userId, stockId, OrderType.BUY, 5, Price(BigDecimal("750.00")))
        trxManager.run {
            val holding = holdingRepo.findByUserAndStock(userId, stockId)
            assertNotNull(holding)
            assertEquals(15, holding.quantity)
        }
    }

    @Test
    fun `BUY fails with InsufficientBalance when user lacks funds`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val result = service.placeOrder(userId, stockId, OrderType.BUY, 100, Price(BigDecimal("15000.00")))
        assertIs<Failure<TradeOrderError>>(result)
        assertIs<TradeOrderError.InsufficientBalance>(result.value)
    }

    @Test
    fun `BUY order has status PENDING`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val result = service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        assertIs<Success<TradeOrder>>(result)
        assertEquals(OrderStatus.PENDING, result.value.status)
    }

    // --- SELL order tests ---

    @Test
    fun `SELL succeeds when user has enough shares`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        trxManager.run { holdingRepo.createHolding(userId, stockId, 10) }
        val result = service.placeOrder(userId, stockId, OrderType.SELL, 5, Price(BigDecimal("750.00")))
        assertIs<Success<TradeOrder>>(result)
        assertEquals(5, result.value.quantity)
        assertEquals(OrderType.SELL, result.value.type)
    }

    @Test
    fun `SELL adds correct amount to balance`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        trxManager.run { holdingRepo.createHolding(userId, stockId, 10) }
        service.placeOrder(userId, stockId, OrderType.SELL, 5, Price(BigDecimal("750.00")))
        trxManager.run {
            val user = userRepo.findById(userId)!!
            assertEquals(0, user.balance.value.compareTo(BigDecimal("10750.00")))
        }
    }

    @Test
    fun `SELL reduces holdings`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        trxManager.run { holdingRepo.createHolding(userId, stockId, 10) }
        service.placeOrder(userId, stockId, OrderType.SELL, 4, Price(BigDecimal("600.00")))
        trxManager.run {
            val holding = holdingRepo.findByUserAndStock(userId, stockId)
            assertNotNull(holding)
            assertEquals(6, holding.quantity)
        }
    }

    @Test
    fun `SELL removes holding entirely when selling all shares`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        trxManager.run { holdingRepo.createHolding(userId, stockId, 10) }
        service.placeOrder(userId, stockId, OrderType.SELL, 10, Price(BigDecimal("1500.00")))
        trxManager.run {
            val holding = holdingRepo.findByUserAndStock(userId, stockId)
            assertNull(holding)
        }
    }

    @Test
    fun `SELL fails with InsufficientHoldings when user has no shares`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val result = service.placeOrder(userId, stockId, OrderType.SELL, 1, Price(BigDecimal("150.00")))
        assertIs<Failure<TradeOrderError>>(result)
        assertIs<TradeOrderError.InsufficientHoldings>(result.value)
    }

    @Test
    fun `SELL fails when trying to sell more than owned`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        trxManager.run { holdingRepo.createHolding(userId, stockId, 5) }
        val result = service.placeOrder(userId, stockId, OrderType.SELL, 10, Price(BigDecimal("1500.00")))
        assertIs<Failure<TradeOrderError>>(result)
        assertIs<TradeOrderError.InsufficientHoldings>(result.value)
    }

    @Test
    fun `SELL order has status PENDING`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        trxManager.run { holdingRepo.createHolding(userId, stockId, 10) }
        val result = service.placeOrder(userId, stockId, OrderType.SELL, 5, Price(BigDecimal("750.00")))
        assertIs<Success<TradeOrder>>(result)
        assertEquals(OrderStatus.PENDING, result.value.status)
    }

    // --- Price capture tests ---

    @Test
    fun `placeOrder captures stock price at order time`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val stock = getTestStock(stockId)
        val result = service.placeOrder(userId, stockId, OrderType.BUY, 10, stock.price)
        assertIs<Success<TradeOrder>>(result)
        assertEquals(BigDecimal("150.00"), result.value.priceValueAtOrder)
    }

    @Test
    fun `priceAtOrder is independent of subsequent stock price changes`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val originalPrice = getTestStock(stockId).price

        val result = service.placeOrder(userId, stockId, OrderType.BUY, 10, originalPrice)
        assertIs<Success<TradeOrder>>(result)
        val capturedPrice = result.value.priceValueAtOrder

        trxManager.run { stockRepo.updatePrice(stockId, BigDecimal("200.00")) }
        assertEquals(BigDecimal("200.00"), getTestStock(stockId).price.value)

        assertEquals(BigDecimal("150.00"), capturedPrice)
    }
}
