package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.user.Balance
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TradeOrderRepositoryMemTest {

    private lateinit var repo: TradeOrderRepository

    @BeforeTest
    fun setup() {
        repo = TradeOrderRepositoryMem()
    }

    private fun createTestUser() = User(
        id = 1L,
        username = Username("testuser"),
        balance = Balance(BigDecimal("10000.00"))
    )

    private fun createTestCompany(
        ticker: String = "AAPL",
        name: String = "Apple Inc."
    ) = Company(
        id = 1L,
        name = CompanyName(name),
        ticker = Ticker(ticker),
        description = Description("Test company"),
        volatility = 0.02,
        drift = 0.001
    )

    private fun createTestStock(
        ticker: String = "AAPL",
        name: String = "Apple Inc.",
        price: BigDecimal = BigDecimal("150.00")
    ) = Stock(
        id = 1L,
        company = createTestCompany(ticker, name),
        price = Price(price)
    )

    @Test
    fun `createOrder returns order with generated id`() {
        val order = repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        assertEquals(1L, order.id)
        assertEquals(OrderType.BUY, order.type)
        assertEquals(10, order.quantity)
        assertEquals(OrderStatus.PENDING, order.status)
    }

    @Test
    fun `createOrder captures current stock price`() {
        val stock = createTestStock()
        val order = repo.createOrder(createTestUser(), stock, OrderType.BUY, 5)
        assertEquals(BigDecimal("150.00"), order.priceValueAtOrder)
    }

    @Test
    fun `createOrder generates sequential ids`() {
        val order1 = repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        val order2 = repo.createOrder(createTestUser(), createTestStock(), OrderType.SELL, 5)
        assertEquals(1L, order1.id)
        assertEquals(2L, order2.id)
    }

    @Test
    fun `findById returns existing order`() {
        val created = repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        val found = repo.findById(created.id)
        assertNotNull(found)
        assertEquals(OrderType.BUY, found.type)
    }

    @Test
    fun `findById returns null for non-existent id`() {
        assertNull(repo.findById(999L))
    }

    @Test
    fun `findAll returns all orders`() {
        repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        repo.createOrder(createTestUser(), createTestStock(), OrderType.SELL, 5)
        assertEquals(2, repo.findAll().size)
    }

    @Test
    fun `findAll returns empty list when no orders`() {
        assertTrue(repo.findAll().isEmpty())
    }

    @Test
    fun `findByUserId returns only that users orders`() {
        val user1 = createTestUser()
        val user2 = User(id = 2L, username = Username("other"), balance = Balance(BigDecimal("5000.00")))
        repo.createOrder(user1, createTestStock(), OrderType.BUY, 10)
        repo.createOrder(user1, createTestStock(), OrderType.SELL, 5)
        repo.createOrder(user2, createTestStock(), OrderType.BUY, 20)
        val user1Orders = repo.findByUserId(user1.id)
        assertEquals(2, user1Orders.size)
        assertTrue(user1Orders.all { it.user.id == user1.id })
    }

    @Test
    fun `findByStockId returns only orders for that stock`() {
        val stock1 = createTestStock(ticker = "AAPL", name = "Apple Inc.")
        val stock2 = Stock(id = 2L, company = createTestCompany("GOOGL", "Alphabet Inc."), price = Price(BigDecimal("2800.00")))
        repo.createOrder(createTestUser(), stock1, OrderType.BUY, 10)
        repo.createOrder(createTestUser(), stock2, OrderType.BUY, 5)
        repo.createOrder(createTestUser(), stock1, OrderType.SELL, 3)
        val stock1Orders = repo.findByStockId(stock1.id)
        assertEquals(2, stock1Orders.size)
        assertTrue(stock1Orders.all { it.stock.id == stock1.id })
    }

    @Test
    fun `findByStatus returns only orders with that status`() {
        val order1 = repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        repo.createOrder(createTestUser(), createTestStock(), OrderType.SELL, 5)
        repo.findById(order1.id)!!.status = OrderStatus.COMPLETED
        val pendingOrders = repo.findByStatus(OrderStatus.PENDING)
        assertEquals(1, pendingOrders.size)
        assertEquals(OrderStatus.PENDING, pendingOrders[0].status)
    }

    @Test
    fun `update with existing id replaces order`() {
        val order = repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        val updated = order.copy(status = OrderStatus.COMPLETED)
        repo.update(updated)
        val found = repo.findById(order.id)!!
        assertEquals(OrderStatus.COMPLETED, found.status)
    }

    @Test
    fun `update with id zero creates new order with generated id`() {
        val order = TradeOrder(
            user = createTestUser(),
            stock = createTestStock(),
            type = OrderType.BUY,
            quantity = 10,
            priceValueAtOrder = BigDecimal("150.00"),
            status = OrderStatus.PENDING
        )
        repo.update(order)
        val all = repo.findAll()
        assertEquals(1, all.size)
        assertEquals(1L, all[0].id)
    }

    @Test
    fun `update with id zero preserves priceAtOrder`() {
        val order = TradeOrder(
            user = createTestUser(),
            stock = createTestStock(),
            type = OrderType.BUY,
            quantity = 10,
            priceValueAtOrder = BigDecimal("199.99"),
            status = OrderStatus.PENDING
        )
        repo.update(order)
        val all = repo.findAll()
        assertEquals(BigDecimal("199.99"), all[0].priceValueAtOrder)
    }

    @Test
    fun `deleteById removes order`() {
        val order = repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        repo.deleteById(order.id)
        assertNull(repo.findById(order.id))
    }

    @Test
    fun `deleteById does not affect other orders`() {
        val order1 = repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        val order2 = repo.createOrder(createTestUser(), createTestStock(), OrderType.SELL, 5)
        repo.deleteById(order1.id)
        assertNull(repo.findById(order1.id))
        assertNotNull(repo.findById(order2.id))
    }

    @Test
    fun `clear removes all orders`() {
        repo.createOrder(createTestUser(), createTestStock(), OrderType.BUY, 10)
        repo.createOrder(createTestUser(), createTestStock(), OrderType.SELL, 5)
        repo.clear()
        assertTrue(repo.findAll().isEmpty())
    }
}
