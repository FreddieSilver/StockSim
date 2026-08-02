package dev.freddiesilver.stocksim.entities

import dev.freddiesilver.stocksim.company.Company
import dev.freddiesilver.stocksim.trading.holding.Holding
import dev.freddiesilver.stocksim.trading.stock.PricePoint
import dev.freddiesilver.stocksim.trading.stock.Stock
import dev.freddiesilver.stocksim.trading.tradeorder.TradeOrder
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.auth.token.Token

fun Company.toJpaEntity(): CompanyEntity =
    CompanyEntity(
        id = id,
        name = name.value,
        ticker = ticker.value,
        description = description.value,
        volatility = volatility,
        baseDrift = drift,
    )

fun Holding.toJpaEntity(): HoldingEntity =
    HoldingEntity(
        id = id,
        user = user.toJpaEntity(),
        stock = stock.toJpaEntity(),
        quantity = quantity,
    )

fun Stock.toJpaEntity(): StockEntity =
    StockEntity(
        id = id,
        company = company.toJpaEntity(),
        price = price.value
    )

fun PricePoint.toJpaEntity(): PricePointEntity =
    PricePointEntity(
        id = id,
        stockId = stockId,
        price = price.value,
        timestamp = timestamp
    )

fun TradeOrder.toJpaEntity(): TradeOrderEntity =
    TradeOrderEntity(
        id = id,
        timestamp = timestamp,
        user = user.toJpaEntity(),
        stock = stock.toJpaEntity(),
        type = type,
        quantity = quantity,
        priceAtOrder = priceValueAtOrder,
        status = status
    )

fun Token.toJpaEntity(): TokenEntity =
    TokenEntity(
        tokenValidationInfo = tokenValidationInfo.validationInfo,
        userId = userId,
        createdAt = createdAt,
        lastUsedAt = lastUsedAt
    )

fun User.toJpaEntity(): UserEntity =
    UserEntity(
        id = id,
        username = username.value,
        email = email.value,
        passwordValidationInfo = passwordValidationInfo.validationInfo,
        balance = balance.value
    )