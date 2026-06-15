package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.user.UserInputDto
import dev.freddiesilver.stocksim.user.UserError
import dev.freddiesilver.stocksim.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/users")
    fun createUser(
        @RequestBody userInputDto: UserInputDto
    ): ResponseEntity<*>{
        val result = userService.createUser(
            username = userInputDto.username,
        )
        return when(result){
            is Success -> ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result.value)
            is Failure ->
                when (result.value){
                    is UserError.UserAlreadyExists -> errorResponse(result.value.message, HttpStatus.BAD_REQUEST)
                    is UserError.InvalidUserData -> errorResponse(result.value.message, HttpStatus.BAD_REQUEST)
                    is UserError.UserNotFound -> errorResponse(result.value.message, HttpStatus.NOT_FOUND)
                    is UserError.InsufficientBalance -> errorResponse(result.value.message, HttpStatus.BAD_REQUEST)
                    is UserError.InvalidDepositAmount -> errorResponse(result.value.message, HttpStatus.BAD_REQUEST)
                }
        }
    }
}