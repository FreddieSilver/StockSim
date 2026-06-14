package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.user.User
import java.math.BigDecimal

interface UserRepository: Repository<User> {
    fun createUser(username: String, balance: BigDecimal): User
    fun findByUsername(username: String): User?
}