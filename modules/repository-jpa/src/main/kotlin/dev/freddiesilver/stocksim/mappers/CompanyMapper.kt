package dev.freddiesilver.stocksim.mappers

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.company.CompanyName
import dev.freddiesilver.stocksim.company.Description
import dev.freddiesilver.stocksim.company.Ticker
import dev.freddiesilver.stocksim.entities.CompanyEntity

object CompanyMapper {

    fun toDomain(entity: CompanyEntity): Company = Company(
        id = entity.id,
        name = CompanyName(entity.name),
        ticker = Ticker(entity.ticker),
        description = Description(entity.description),
        volatility = entity.volatility,
        drift = entity.baseDrift
    )

    fun toEntity(domain: Company): CompanyEntity = CompanyEntity(
        id = domain.id,
        name = domain.name.value,
        ticker = domain.ticker.value,
        description = domain.description.value,
        volatility = domain.volatility,
        baseDrift = domain.drift
    )
}