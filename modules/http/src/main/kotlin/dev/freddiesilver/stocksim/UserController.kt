package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.user.input.UserCreateDto
import dev.freddiesilver.stocksim.dto.user.input.UserLoginDto
import dev.freddiesilver.stocksim.dto.user.output.UserHomeResponseDto
import dev.freddiesilver.stocksim.dto.user.output.UserLoginResponseDto
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
    ): ResponseEntity<UserLoginResponseDto> {
        val result = authService.registerUser(input.username, input.email, input.password)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserLoginResponseDto(result.token))
    }

    @PostMapping("/users/login")
    fun login(
        @RequestBody input: UserLoginDto,
    ): ResponseEntity<UserLoginResponseDto> {
        val result = authService.login(input.email, input.password)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Authorization", "Bearer ${result.token}")
            .body(UserLoginResponseDto(result.token))
    }

    @PostMapping("/users/logout")
    fun logout(user: AuthenticatedUser): ResponseEntity<Unit> {
        authService.revokeToken(user.token)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/me")
    fun userHome(user: AuthenticatedUser): ResponseEntity<UserHomeResponseDto> {
        val result = userService.getUserById(user.user.id)
        return ResponseEntity.ok(
            UserHomeResponseDto(
                id = result.id,
                username = result.username.value,
                email = result.email.value,
                balance = result.balance.value.toDouble(),
            ),
        )
    }
}
