package dev.freddiesilver.stocksim.mappers

import dev.freddiesilver.stocksim.entities.UserEntity
import dev.freddiesilver.stocksim.user.Balance
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username

object UserMapper {

    fun toDomain(entity: UserEntity): User = User(
        id = entity.id,
        username = Username(entity.username),
        balance = Balance(entity.balance)
    )

    fun toEntity(domain: User): UserEntity = UserEntity(
        id = domain.id,
        username = domain.username.value,
        balance = domain.balance.value
    )
}