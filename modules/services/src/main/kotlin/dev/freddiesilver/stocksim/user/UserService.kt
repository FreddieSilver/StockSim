package dev.freddiesilver.stocksim.user

import dev.freddiesilver.stocksim.UserRepository
import dev.freddiesilver.stocksim.user.error.UserError
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@Transactional
class UserService(
    private val userRepo: UserRepository,
) {
    fun createUser(
        username: String,
        email: String,
        password: String,
    ): User {
        if (userRepo.findByEmail(email) != null) throw UserError.UserAlreadyExists()
        val user =
            userRepo.createUser(
                Username(username),
                Email(email),
                PasswordValidationInfo(password),
            )
        return user
    }

    fun getUserById(id: Long): User {
        val user = userRepo.findById(id)
        return user ?: throw UserError.UserNotFound()
    }

    fun deposit(
        userId: Long,
        amount: BigDecimal,
    ): User {
        val user = userRepo.findById(userId) ?: throw UserError.UserNotFound()
        user.deposit(amount)
        userRepo.update(user)
        return user
    }

    fun withdraw(
        userId: Long,
        amount: BigDecimal,
    ): User {
        val user = userRepo.findById(userId) ?: throw UserError.UserNotFound()
        user.withdraw(amount)
        userRepo.update(user)
        return user
    }
}
