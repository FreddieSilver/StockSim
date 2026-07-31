package dev.freddiesilver.stocksim.tradeorder

import dev.freddiesilver.stocksim.HoldingRepository
import dev.freddiesilver.stocksim.HoldingRepositoryMem
import dev.freddiesilver.stocksim.StockRepository
import dev.freddiesilver.stocksim.StockRepositoryMem
import dev.freddiesilver.stocksim.TradeOrderRepository
import dev.freddiesilver.stocksim.TradeOrderRepositoryMem
import dev.freddiesilver.stocksim.UserRepository
import dev.freddiesilver.stocksim.UserRepositoryMem
import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import dev.freddiesilver.stocksim.tradeorder.error.TradeOrderError
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class TradeOrderServiceTest {
    private lateinit var service: TradeOrderService
    private lateinit var userRepo: UserRepository
    private lateinit var stockRepo: StockRepository
    private lateinit var holdingRepo: HoldingRepository
    private lateinit var tradeOrderRepo: TradeOrderRepository

    @BeforeTest
    fun setup() {
        userRepo = UserRepositoryMem()
        stockRepo = StockRepositoryMem()
        holdingRepo = HoldingRepositoryMem()
        tradeOrderRepo = TradeOrderRepositoryMem()
        service = TradeOrderService(userRepo, stockRepo, holdingRepo, tradeOrderRepo)
    }

    private fun createTestUser(): Long {
        val user =
            userRepo.createUser(
                dev.freddiesilver.stocksim.user.Username("testuser"),
                dev.freddiesilver.stocksim.user.Email("testuser@example.com"),
                dev.freddiesilver.stocksim.user.PasswordValidationInfo("hashed_pw"),
            )
        user.deposit(BigDecimal("10000.00"))
        userRepo.update(user)
        return user.id
    }

    private fun createTestCompany() =
        Company(
            id = 1L,
            name = CompanyName("Apple Inc."),
            ticker = Ticker("AAPL"),
            description = Description("Technology company"),
            volatility = 0.02,
            drift = 0.001,
        )

    private fun createTestStock(): Long {
        val stock = stockRepo.createStock(createTestCompany(), BigDecimal("150.00"))
        return stock.id
    }

    private fun getTestStock(stockId: Long) = stockRepo.findById(stockId)!!

    @Test
    fun `placeOrder throws when quantity is zero`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        assertFailsWith<TradeOrderError.InvalidOrderDetails> {
            service.placeOrder(
                userId,
                stockId,
                OrderType.BUY,
                0,
                Price(BigDecimal("1500.00")),
            )
        }
    }

    @Test
    fun `placeOrder throws when quantity is negative`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        assertFailsWith<TradeOrderError.InvalidOrderDetails> {
            service.placeOrder(userId, stockId, OrderType.BUY, -5, Price(BigDecimal("1500.00")))
        }
    }

    @Test
    fun `placeOrder throws when user is not found`() {
        val stockId = createTestStock()
        assertFailsWith<TradeOrderError.UserNotFound> {
            service.placeOrder(999L, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        }
    }

    @Test
    fun `placeOrder throws when stock is not found`() {
        val userId = createTestUser()
        assertFailsWith<TradeOrderError.StockNotFound> {
            service.placeOrder(userId, 999L, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        }
    }

    // --- BUY order tests ---

    @Test
    fun `BUY order succeeds with sufficient balance`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val order = service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        assertEquals(10, order.quantity)
        assertEquals(OrderType.BUY, order.type)
    }

    @Test
    fun `BUY with quantity of one succeeds`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val order = service.placeOrder(userId, stockId, OrderType.BUY, 1, Price(BigDecimal("150.00")))
        assertEquals(1, order.quantity)
    }

    @Test
    fun `BUY deducts correct amount from balance`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        assertEquals(0, userRepo.findById(userId)!!.balance.value.compareTo(BigDecimal("8500.00")))
    }

    @Test
    fun `BUY adds holding for purchased stock`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        val holding = holdingRepo.findByUserIdAndStockId(userId, stockId)
        assertNotNull(holding)
        assertEquals(10, holding.quantity)
    }

    @Test
    fun `BUY accumulates quantity when holding already exists`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        service.placeOrder(userId, stockId, OrderType.BUY, 5, Price(BigDecimal("750.00")))
        val holding = holdingRepo.findByUserIdAndStockId(userId, stockId)
        assertNotNull(holding)
        assertEquals(15, holding.quantity)
    }

    @Test
    fun `BUY throws InsufficientBalance when user lacks funds`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        assertFailsWith<TradeOrderError.InsufficientBalance> {
            service.placeOrder(userId, stockId, OrderType.BUY, 100, Price(BigDecimal("15000.00")))
        }
    }

    @Test
    fun `BUY order has status PENDING`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val order = service.placeOrder(userId, stockId, OrderType.BUY, 10, Price(BigDecimal("1500.00")))
        assertEquals(OrderStatus.PENDING, order.status)
    }

    @Test
    fun `SELL succeeds when user has enough shares`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        holdingRepo.createHolding(userId, stockId, 10)
        val order = service.placeOrder(userId, stockId, OrderType.SELL, 5, Price(BigDecimal("750.00")))
        assertEquals(5, order.quantity)
        assertEquals(OrderType.SELL, order.type)
    }

    @Test
    fun `SELL adds correct amount to balance`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        holdingRepo.createHolding(userId, stockId, 10)
        service.placeOrder(userId, stockId, OrderType.SELL, 5, Price(BigDecimal("750.00")))
        val user = userRepo.findById(userId)!!
        assertEquals(0, user.balance.value.compareTo(BigDecimal("10750.00")))
    }

    @Test
    fun `SELL reduces holdings`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        holdingRepo.createHolding(userId, stockId, 10)
        service.placeOrder(userId, stockId, OrderType.SELL, 4, Price(BigDecimal("600.00")))
        val holding = holdingRepo.findByUserIdAndStockId(userId, stockId)
        assertNotNull(holding)
        assertEquals(6, holding.quantity)
    }

    @Test
    fun `SELL throws InsufficientHoldings when user has no shares`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        assertFailsWith<TradeOrderError.InsufficientHoldings> {
            service.placeOrder(userId, stockId, OrderType.SELL, 1, Price(BigDecimal("150.00")))
        }
    }

    @Test
    fun `SELL throws when trying to sell more than owned`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        holdingRepo.createHolding(userId, stockId, 5)
        assertFailsWith<TradeOrderError.InsufficientHoldings> {
            service.placeOrder(userId, stockId, OrderType.SELL, 10, Price(BigDecimal("1500.00")))
        }
    }

    @Test
    fun `SELL order has status PENDING`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        holdingRepo.createHolding(userId, stockId, 10)
        val order = service.placeOrder(userId, stockId, OrderType.SELL, 5, Price(BigDecimal("750.00")))
        assertEquals(OrderStatus.PENDING, order.status)
    }

    @Test
    fun `placeOrder captures stock price at order time`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val stock = getTestStock(stockId)
        val order = service.placeOrder(userId, stockId, OrderType.BUY, 10, stock.price)
        assertEquals(BigDecimal("150.00"), order.priceValueAtOrder)
    }

    @Test
    fun `priceAtOrder is independent of subsequent stock price changes`() {
        val userId = createTestUser()
        val stockId = createTestStock()
        val originalPrice = getTestStock(stockId).price

        val order = service.placeOrder(userId, stockId, OrderType.BUY, 10, originalPrice)
        val capturedPrice = order.priceValueAtOrder

        stockRepo.updatePrice(stockId, BigDecimal("200.00"))
        assertEquals(BigDecimal("200.00"), getTestStock(stockId).price.value)

        assertEquals(BigDecimal("150.00"), capturedPrice)
    }
}
