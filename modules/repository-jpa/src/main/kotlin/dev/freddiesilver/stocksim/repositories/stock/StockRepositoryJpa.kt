package dev.freddiesilver.stocksim.repositories.stock

import dev.freddiesilver.stocksim.StockRepository
import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.repositories.company.CompanyJpaRepository
import dev.freddiesilver.stocksim.entities.StockEntity
import dev.freddiesilver.stocksim.mappers.StockMapper
import dev.freddiesilver.stocksim.trading.stock.Stock
import java.math.BigDecimal


class StockRepositoryJpa(
    private val jpa: StockJpaRepository,
    private val companyJpaRepository: CompanyJpaRepository
) : StockRepository {

    override fun createStock(ticker: String, company: Company, initialPrice: BigDecimal): Stock {
        val companyEntity = companyJpaRepository.findById(company.id).orElseThrow {
            IllegalArgumentException("Company with id ${company.id} not found")
        }
        val entity = jpa.save(
            StockEntity(
                company = companyEntity,
                price = initialPrice
            )
        )
        return StockMapper.toDomain(entity)
    }

    override fun updateAllPrices(stocks: List<Stock>) {
        stocks.forEach { stock ->
            val entity = jpa.findById(stock.id).orElseThrow {
                IllegalArgumentException("Stock with id ${stock.id} not found")
            }
            entity.price = stock.price.value
            jpa.save(entity)
        }
    }

    override fun updatePrice(id: Long, newPrice: BigDecimal) {
        val entity = jpa.findById(id).orElseThrow {
            IllegalArgumentException("Stock with id $id not found")
        }
        entity.price = newPrice
        jpa.save(entity)
    }

    override fun findById(id: Long): Stock? =
        jpa.findById(id).orElse(null)?.let { StockMapper.toDomain(it) }

    override fun findAll(): List<Stock> =
        jpa.findAll().map { StockMapper.toDomain(it) }

    override fun update(entity: Stock) {
        val existing = jpa.findById(entity.id).orElseThrow {
            IllegalArgumentException("Stock with id ${entity.id} not found")
        }
        existing.price = entity.price.value
        jpa.save(existing)
    }

    override fun deleteById(id: Long) =
        jpa.deleteById(id)

    override fun clear() =
        jpa.deleteAll()
}
