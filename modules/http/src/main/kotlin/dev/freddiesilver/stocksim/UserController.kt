package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.toDto
import dev.freddiesilver.stocksim.dto.user.input.DepositDto
import dev.freddiesilver.stocksim.dto.user.input.UserCreateDto
import dev.freddiesilver.stocksim.dto.user.input.UserLoginDto
import dev.freddiesilver.stocksim.dto.user.output.DepositResponseDto
import dev.freddiesilver.stocksim.dto.user.output.UserDto
import dev.freddiesilver.stocksim.dto.user.output.TokenDto
import dev.freddiesilver.stocksim.helpers.dataResponse
import dev.freddiesilver.stocksim.user.AuthService
import dev.freddiesilver.stocksim.user.UserService
import dev.freddiesilver.stocksim.user.auth.AuthenticatedUser
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
) {
    @PostMapping("/users")
    fun createUser(
        @RequestBody input: UserCreateDto,
    ): ResponseEntity<*> {
        val result = authService.registerUser(input.username, input.email, input.password)
        return dataResponse(
            status = HttpStatus.CREATED,
            data = TokenDto(result.token)
        )
    }

    @PostMapping("/users/login")
    fun login(
        @RequestBody input: UserLoginDto,
    ): ResponseEntity<*> {
        val result = authService.login(input.email, input.password)
        return dataResponse(
            status = HttpStatus.OK,
            token = result.token,
            data = TokenDto(result.token)
        )
    }

    @PostMapping("/users/logout")
    fun logout(user: AuthenticatedUser): ResponseEntity<*> {
        authService.revokeToken(user.token)
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

}
