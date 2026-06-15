package dev.freddiesilver.stocksim.trading

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.trading.stock.Price
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.company.Ticker
import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.user.Balance
import dev.freddiesilver.stocksim.user.Email
import dev.freddiesilver.stocksim.user.PasswordValidationInfo
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class TradeOrderTest {

    private fun createTestUser() = User(
        id = 1L,
        username = Username("testuser"),
        email = Email("testuser@example.com"),
        passwordValidationInfo = PasswordValidationInfo("hashed_pw"),
        balance = Balance(BigDecimal("10000.00"))
    )

    private fun createTestCompany() = Company(
        id = 1L,
        name = CompanyName("Apple Inc."),
        ticker = Ticker("AAPL"),
        description = Description("Technology company"),
        volatility = 0.02,
        drift = 0.001
    )

    private fun createTestStock() = Stock(
        id = 1L,
        company = createTestCompany(),
        price = Price(BigDecimal("150.00"))
    )

    @Test
    fun `trade order is created with correct fields`() {
        val order = TradeOrder(
            id = 1L,
            user = createTestUser(),
            stock = createTestStock(),
            type = OrderType.BUY,
            quantity = 10,
            priceValueAtOrder = BigDecimal("150.00"),
            status = OrderStatus.PENDING
        )
        assertEquals(1L, order.id)
        assertEquals(OrderType.BUY, order.type)
        assertEquals(10, order.quantity)
        assertEquals(OrderStatus.PENDING, order.status)
        assertEquals(BigDecimal("150.00"), order.priceValueAtOrder)
    }

    @Test
    fun `trade order with default id is created`() {
        val order = TradeOrder(
            user = createTestUser(),
            stock = createTestStock(),
            type = OrderType.SELL,
            quantity = 5,
            priceValueAtOrder = BigDecimal("200.00"),
            status = OrderStatus.COMPLETED
        )
        assertEquals(0L, order.id)
    }

    @Test
    fun `trade order status can be changed`() {
        val order = TradeOrder(
            id = 1L,
            user = createTestUser(),
            stock = createTestStock(),
            type = OrderType.BUY,
            quantity = 10,
            priceValueAtOrder = BigDecimal("150.00"),
            status = OrderStatus.PENDING
        )
        order.status = OrderStatus.COMPLETED
        assertEquals(OrderStatus.COMPLETED, order.status)

        order.status = OrderStatus.FAILED
        assertEquals(OrderStatus.FAILED, order.status)
    }
}
