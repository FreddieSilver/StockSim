package dev.freddiesilver.stocksim.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

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
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(name = "stock_id", nullable = false)
    val stockId: Long,
    @Column(nullable = false)
    var quantity: Int,
)
