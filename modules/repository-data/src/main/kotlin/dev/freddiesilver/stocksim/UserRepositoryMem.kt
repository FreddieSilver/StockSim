package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import dev.freddiesilver.stocksim.user.Balance
import java.math.BigDecimal

class UserRepositoryMem: UserRepository {
    private val users = mutableListOf<User>()
    private var nextId = 1L
    override fun createUser(username: String, balance: BigDecimal): User =
        User(
            id = nextId++,
            username = Username(username),
            balance = Balance(balance)
        ).also { users.add(it) }

    override fun findByUsername(username: String): User? =
        users.firstOrNull { it.username.value == username }


    override fun findById(id: Long): User? =
        users.firstOrNull { it.id == id }


    override fun findAll(): List<User> =
        users.toList()

    override fun update(entity: User){
        if (entity.id == 0L) {
            val newUser = User(
                id = nextId++,
                username = entity.username,
                balance = entity.balance
            )
            users.add(newUser)
        } else {
            users.removeIf { it.id == entity.id }
            users.add(entity)
        }
    }

    override fun deleteById(id: Long) {
        users.removeIf { it.id == id }
    }

    override fun clear() =
        users.clear()
}