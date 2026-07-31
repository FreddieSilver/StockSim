package dev.freddiesilver.stocksim.dto.stock

import dev.freddiesilver.stocksim.dto.company.CompanyDto

data class StockDto(
    val id: Long,
    val company: CompanyDto,
    val price: Double
)