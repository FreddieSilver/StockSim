package dev.freddiesilver.stocksim.entities

import dev.freddiesilver.stocksim.trading.tradeorder.OrderStatus
import dev.freddiesilver.stocksim.trading.tradeorder.OrderType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "trade_orders")
class TradeOrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // Notice we link to the UserEntity and StockEntity directly here
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    val stock: StockEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: OrderType,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = false, precision = 19, scale = 4)
    val priceAtOrder: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus
)