package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.ApiErrorResponse
import dev.freddiesilver.stocksim.stock.error.StockError
import dev.freddiesilver.stocksim.tradeorder.error.TradeOrderError
import dev.freddiesilver.stocksim.user.error.AuthError
import dev.freddiesilver.stocksim.user.error.UserError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    // catch init requires
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiErrorResponse> {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid input")
    }

    // authErrors
    @ExceptionHandler(AuthError::class)
    fun handleAuthError(ex: AuthError): ResponseEntity<ApiErrorResponse> {
        val status =
            when (ex) {
                is AuthError.InvalidRegistrationData -> HttpStatus.UNAUTHORIZED
                is AuthError.IncorrectCredentials -> HttpStatus.BAD_REQUEST
                is AuthError.BadPassword -> HttpStatus.BAD_REQUEST
                is AuthError.EmailInUse -> HttpStatus.BAD_REQUEST
                is AuthError.CredentialsMissing -> HttpStatus.BAD_REQUEST
            }
        return buildResponse(status, ex.message)
    }

    // userErrors
    @ExceptionHandler(UserError::class)
    fun handleUserError(ex: UserError): ResponseEntity<ApiErrorResponse> {
        val status =
            when (ex) {
                is UserError.UserNotFound -> HttpStatus.NOT_FOUND
                is UserError.UserAlreadyExists -> HttpStatus.CONFLICT
                is UserError.InsufficientBalance -> HttpStatus.PAYMENT_REQUIRED
                else -> HttpStatus.BAD_REQUEST
            }
        return buildResponse(status, ex.message)
    }

    // stockErrors
    @ExceptionHandler(StockError::class)
    fun handleStockError(ex: StockError): ResponseEntity<ApiErrorResponse> {
        val status =
            when (ex) {
                is StockError.StockNotFound -> HttpStatus.NOT_FOUND
                else -> HttpStatus.BAD_REQUEST
            }
        return buildResponse(status, ex.message ?: "Stock error")
    }

    // tradeOrderErrors
    @ExceptionHandler(TradeOrderError::class)
    fun handleTradeOrderError(ex: TradeOrderError): ResponseEntity<ApiErrorResponse> {
        val status =
            when (ex) {
                is TradeOrderError.StockNotFound,
                is TradeOrderError.UserNotFound,
                -> HttpStatus.NOT_FOUND
                is TradeOrderError.InsufficientBalance,
                is TradeOrderError.InsufficientHoldings,
                -> HttpStatus.BAD_REQUEST
                else -> HttpStatus.BAD_REQUEST
            }
        return buildResponse(status, ex.message ?: "Trade order error")
    }

    // internal error
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiErrorResponse> {
        println("INTERNAL ERROR: ${ex.message}")
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred.")
    }

    private fun buildResponse(
        status: HttpStatus,
        message: String,
    ): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(status).body(
            ApiErrorResponse(
                error = message,
                status = status.value(),
            ),
        )
    }
}
