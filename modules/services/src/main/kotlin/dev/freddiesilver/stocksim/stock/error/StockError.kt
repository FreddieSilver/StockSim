package dev.freddiesilver.stocksim.stock.error

sealed class StockError(
    override val message: String?,
) : Exception(message) {
    class StockNotFound : StockError("Stock not found")

    class InvalidStockData(
        additionalMessage: String,
    ) : StockError("Invalid stock data: $additionalMessage")
}
