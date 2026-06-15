package dev.freddiesilver.stocksim.mappers

import dev.freddiesilver.stocksim.entities.UserEntity
import dev.freddiesilver.stocksim.user.Balance
import dev.freddiesilver.stocksim.user.Email
import dev.freddiesilver.stocksim.user.PasswordValidationInfo
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username

object UserMapper {

    fun toDomain(entity: UserEntity): User = User(
        id = entity.id,
        username = Username(entity.username),
        email = Email(entity.email),
        passwordValidationInfo = PasswordValidationInfo(entity.passwordValidationInfo),
        balance = Balance(entity.balance)
    )

    fun toEntity(domain: User): UserEntity = UserEntity(
        id = domain.id,
        username = domain.username.value,
        email = domain.email.value,
        passwordValidationInfo = domain.passwordValidationInfo.validationInfo,
        balance = domain.balance.value
    )
}
