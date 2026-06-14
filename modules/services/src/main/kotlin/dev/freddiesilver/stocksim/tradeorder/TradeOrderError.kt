package dev.freddiesilver.stocksim.tradeorder

sealed class TradeOrderError(
    override val message: String?
): Exception(message) {
    class StockNotFound : TradeOrderError("Stock not found")

    class InvalidOrderDetails(
        additionalMessage: String,
    ) : TradeOrderError("Order details invalid: $additionalMessage")

    class UserNotFound: TradeOrderError("User not found")

    class InsufficientBalance(
        additionalMessage: String,
    ) : TradeOrderError("Insufficient balance: $additionalMessage")

    class InsufficientHoldings(
        additionalMessage: String,
    ) : TradeOrderError("Insufficient holdings: $additionalMessage")
}