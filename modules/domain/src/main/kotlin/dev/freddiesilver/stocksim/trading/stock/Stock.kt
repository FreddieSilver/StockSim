package dev.freddiesilver.stocksim.trading.stock

import dev.freddiesilver.stocksim.company.Company

data class Stock (
    val id: Long = 0,
    val company: Company,
    var price: Price
)