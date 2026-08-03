package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.toDto
import dev.freddiesilver.stocksim.dto.user.input.DepositDto
import dev.freddiesilver.stocksim.dto.user.input.UserCreateDto
import dev.freddiesilver.stocksim.dto.user.input.UserLoginDto
import dev.freddiesilver.stocksim.dto.user.output.DepositResponseDto
import dev.freddiesilver.stocksim.dto.user.output.UserDto
import dev.freddiesilver.stocksim.dto.user.output.TokenDto
import dev.freddiesilver.stocksim.helpers.dataResponse
import dev.freddiesilver.stocksim.tradeorder.TradeOrderService
import dev.freddiesilver.stocksim.user.AuthService
import dev.freddiesilver.stocksim.user.UserService
import dev.freddiesilver.stocksim.user.auth.AuthenticatedUser
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val authService: AuthService,
    private val userService: UserService,
    private val tradeOrderService: TradeOrderService
) {
    @PostMapping("/users")
    fun createUser(
        @RequestBody input: UserCreateDto,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val result = authService.registerUser(input.username, input.email, input.password)
        setTokenCookie(response, result.token)
        return dataResponse(
            status = HttpStatus.CREATED,
            data = TokenDto(result.token)
        )
    }

    @PostMapping("/users/login")
    fun login(
        @RequestBody input: UserLoginDto,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val result = authService.login(input.email, input.password)
        setTokenCookie(response, result.token)
        return dataResponse(
            status = HttpStatus.OK,
            token = result.token,
            data = TokenDto(result.token)
        )
    }

    private fun setTokenCookie(response: HttpServletResponse, token: String) {
        val cookie = Cookie("token", token).apply {
            isHttpOnly = true
            path = "/"
            maxAge = 24 * 60 * 60 // 24 hours
            // secure = true // uncomment when https
        }
        response.addCookie(cookie)
    }

    private fun clearTokenCookie(response: HttpServletResponse) {
        val cookie = Cookie("token", "").apply {
            isHttpOnly = true
            path = "/"
            maxAge = 0 // instantly deletes the cookie
        }
        response.addCookie(cookie)
    }

    @PostMapping("/users/logout")
    fun logout(user: AuthenticatedUser, response: HttpServletResponse): ResponseEntity<*> {
        authService.revokeToken(user.token)
        clearTokenCookie(response)
        return dataResponse<Unit>(
            status = HttpStatus.NO_CONTENT,
        )
    }

    @GetMapping("/me")
    fun userHome(user: AuthenticatedUser): ResponseEntity<UserDto> {
        val result = userService.getUserById(user.user.id)
        return dataResponse(
            status = HttpStatus.OK,
            data = result.toDto()
        )
    }

    @PostMapping("/me/deposit")
    fun deposit(user: AuthenticatedUser, @RequestBody input: DepositDto): ResponseEntity<*> {
        val result = userService.deposit(user.user.id, input.amount)
        return dataResponse(
            status = HttpStatus.OK,
            data = DepositResponseDto(result.balance.value.toDouble())
        )
    }

    @GetMapping("/me/orders")
    fun getTradeOrders(
        user: AuthenticatedUser
    ): ResponseEntity<*> {
        val tradeOrders = tradeOrderService.getTradeOrdersForUser(user.user.id)
        return dataResponse(HttpStatus.OK,tradeOrders.map { it.toDto() })
    }

    @GetMapping("/me/holdings")
    fun getHoldings(
        user: AuthenticatedUser
    ): ResponseEntity<*> {
        val tradeOrders = tradeOrderService.getHoldingsForUser(user.user.id)
        return dataResponse(HttpStatus.OK, tradeOrders.map { it.toDto() }.reversed())
    }
}
