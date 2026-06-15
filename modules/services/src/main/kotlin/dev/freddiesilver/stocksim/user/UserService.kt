package dev.freddiesilver.stocksim.user

import dev.freddiesilver.stocksim.Either
import dev.freddiesilver.stocksim.failure
import dev.freddiesilver.stocksim.success
import dev.freddiesilver.stocksim.transaction.TransactionManager
import org.springframework.stereotype.Service
import java.math.BigDecimal


@Service
class UserService(private val trxManager: TransactionManager) {

    fun createUser(username: String): Either<UserError, User> =
        trxManager.run {
            val existingUser = userRepo.findByUsername(username)
            if (existingUser != null) {
                return@run failure(UserError.UserAlreadyExists())
            }
            try {
                val newUser = userRepo.createUser(username, STARTING_BALANCE)
                return@run success(newUser)
            } catch (e: Exception) {
                return@run failure(UserError.InvalidUserData(e.message ?: "Unknown error"))
            }
        }

    fun getUserByUsername(username: String): Either<UserError, User> =
        trxManager.run {
            val user = userRepo.findByUsername(username)
            if (user != null) {
                success(user)
            } else {
                failure(UserError.UserNotFound())
            }
        }

    fun getUserById(id: Long): Either<UserError, User> =
        trxManager.run {
            val user = userRepo.findById(id)
            if (user != null) {
                success(user)
            } else {
                failure(UserError.UserNotFound())
            }
        }

    fun deposit(userId: Long, amount: BigDecimal): Either<UserError, User> =
        trxManager.run {
            val user = userRepo.findById(userId)
                ?: return@run failure(UserError.UserNotFound())
            try {
                user.deposit(amount)
                userRepo.update(user)
                success(user)
            } catch (e: Exception) {
                failure(UserError.InvalidDepositAmount(e.message ?: "Unknown error"))
            }
        }

    fun withdraw(userId: Long, amount: BigDecimal): Either<UserError, User> =
        trxManager.run {
            val user = userRepo.findById(userId)
                ?: return@run failure(UserError.UserNotFound())
            try {
                user.withdraw(amount)
                userRepo.update(user)
                success(user)
            } catch (e: Exception) {
                failure(UserError.InsufficientBalance(e.message ?: "Unknown error"))
            }
        }

    companion object{
        private val STARTING_BALANCE = BigDecimal(0)
    }
}
