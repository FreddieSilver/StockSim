package dev.freddiesilver.stocksim.dto

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.dto.company.CompanyDto
import dev.freddiesilver.stocksim.dto.holding.HoldingDto
import dev.freddiesilver.stocksim.dto.stock.PricePointDto
import dev.freddiesilver.stocksim.dto.stock.StockDto
import dev.freddiesilver.stocksim.dto.tradeorder.TradeOrderDto
import dev.freddiesilver.stocksim.dto.user.output.UserDto
import dev.freddiesilver.stocksim.trading.holding.Holding
import dev.freddiesilver.stocksim.trading.stock.PricePoint
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.user.User
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy ").withZone(ZoneId.systemDefault())

fun Company.toDto(): CompanyDto =
    CompanyDto(
        id = id,
        name = name.value,
        ticker = ticker.value,
        description = description.value,
        volatility = volatility,
        drift = drift,
    )

fun Stock.toDto(): StockDto =
    StockDto(
        id = id,
        company = company.toDto(),
        price = price.value.toDouble()
    )


fun User.toDto(): UserDto =
    UserDto(
        id = id,
        username = username.value,
        email = email.value,
        balance = balance.value.toDouble(),
    )


fun TradeOrder.toDto(): TradeOrderDto =
    TradeOrderDto(
        id = id,
        timestamp = timeFormatter.format(this.timestamp),
        stock = stock.toDto(),
        type = type.name,
        quantity = quantity.toDouble(),
        priceValueAtOrder = priceValueAtOrder.toDouble(),
        status = status.name
    )

fun PricePoint.toDto(): PricePointDto =
    PricePointDto(
        timestamp = timeFormatter.format(this.timestamp),
        price = price.value.toDouble()
    )

fun Holding.toDto(): HoldingDto =
    HoldingDto(
        id = id,
        user = user.toDto(),
        stock = stock.toDto(),
        quantity = quantity.toDouble()
    )
