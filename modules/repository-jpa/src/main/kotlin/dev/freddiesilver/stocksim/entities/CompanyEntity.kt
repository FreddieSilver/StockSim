package dev.freddiesilver.stocksim.entities

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "companies")
class CompanyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val name: String,
    @Column(unique = true, nullable = false, length = 10)
    val ticker: String,
    @Column(nullable = false, length = 1000)
    val description: String,
    @Column(nullable = false)
    val volatility: Double,
    @Column(nullable = false)
    val baseDrift: Double,
){
    fun toDomain(): Company {
        return Company(
            id = id,
            name = CompanyName(name),
            ticker = Ticker(ticker),
            description = Description(description),
            volatility = volatility,
            drift = baseDrift,
        )
    }
}
