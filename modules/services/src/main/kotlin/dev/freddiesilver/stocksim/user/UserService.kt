package dev.freddiesilver.stocksim.user

import dev.freddiesilver.stocksim.Either
import dev.freddiesilver.stocksim.UserRepository
import dev.freddiesilver.stocksim.failure
import dev.freddiesilver.stocksim.success
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
    ): Either<UserError, User> {
        if (userRepo.findByEmail(email) != null) return failure(UserError.UserAlreadyExists())
        val user =
            userRepo.createUser(
                Username(username),
                Email(email),
                PasswordValidationInfo(password),
            )
        return success(user)
    }


    fun getUserById(id: Long): Either<UserError, User> {
        val user = userRepo.findById(id)
        return if (user != null) success(user) else failure(UserError.UserNotFound())
    }

        fun deposit(
            userId: Long,
            amount: BigDecimal,
        ): Either<UserError, User> {
            val user = userRepo.findById(userId) ?: return failure(UserError.UserNotFound())
            user.deposit(amount)
            userRepo.update(user)
            return success(user)
        }

        fun withdraw(
            userId: Long,
            amount: BigDecimal,
        ): Either<UserError, User> {
            val user = userRepo.findById(userId) ?: return failure(UserError.UserNotFound())
            user.withdraw(amount)
            userRepo.update(user)
            return success(user)
        }

    }
