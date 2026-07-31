package dev.freddiesilver.stocksim.entities

import dev.freddiesilver.stocksim.trading.holding.Holding
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal

@Entity
@Table(
    name = "holdings",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "stock_id"]), // a user can only have one holding record per stock
    ],
)
class HoldingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    val stock: StockEntity,
    @Column(nullable = false, precision = 19, scale = 4)
    var quantity: BigDecimal,
){
    fun toDomain(): Holding {
        return Holding(
            id = id,
            user = user.toDomain(),
            stock = stock.toDomain(),
            quantity = quantity,
        )
    }
}
