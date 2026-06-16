package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.`in`.UserCreateDto
import dev.freddiesilver.stocksim.dto.`in`.UserLoginDto
import dev.freddiesilver.stocksim.dto.out.UserHomeResponseDto
import dev.freddiesilver.stocksim.dto.out.UserLoginResponseDto
import dev.freddiesilver.stocksim.user.AuthService
import dev.freddiesilver.stocksim.user.error.AuthError
import dev.freddiesilver.stocksim.user.error.UserError
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
    private val userService: UserService
) {

    @PostMapping("/users")
    fun createUser(@RequestBody input: UserCreateDto): ResponseEntity<*> {
        return when (val result = authService.registerUser(input.username, input.email, input.password)) {
            is Success -> ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapOf("token" to result.value.token))

            is Failure ->
                when (val error = result.value) {
                    is AuthError.BadPassword -> errorResponse(error.message, HttpStatus.BAD_REQUEST)
                    is AuthError.EmailInUse -> errorResponse(error.message, HttpStatus.BAD_REQUEST)
                    is AuthError.UserOrPasswordAreInvalid -> errorResponse(error.message, HttpStatus.BAD_REQUEST)
                }
        }
    }

    @PostMapping("/users/login")
    fun login(@RequestBody input: UserLoginDto): ResponseEntity<*> {
        return when (val result = authService.login(input.email, input.password)) {
            is Success -> {
                ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Authorization", "Bearer ${result.value.token}")
                    .body(UserLoginResponseDto(result.value.token))
            }
            is Failure ->
                when (val error = result.value) {
                    is AuthError.UserOrPasswordAreInvalid -> errorResponse(error.message, HttpStatus.UNAUTHORIZED)
                    else -> errorResponse("Authentication failed", HttpStatus.BAD_REQUEST)
                }
        }
    }

    @PostMapping("/users/logout")
    fun logout(user: AuthenticatedUser): ResponseEntity<*> {
        authService.revokeToken(user.token)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Unit>()
    }

    @GetMapping("/me")
    fun userHome(user: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = userService.getUserById(user.user.id)) {
            is Success ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                        UserHomeResponseDto(
                            id = res.value.id,
                            name = res.value.username.value,
                            email = res.value.email.value,
                            balance = res.value.balance.value.toDouble(),
                        )
                    )
            is Failure ->
                when (val error = res.value) {
                    is UserError.UserNotFound -> errorResponse(error.message, HttpStatus.NOT_FOUND)
                    else -> errorResponse(error.message, HttpStatus.INTERNAL_SERVER_ERROR)
                }
        }
    }
}