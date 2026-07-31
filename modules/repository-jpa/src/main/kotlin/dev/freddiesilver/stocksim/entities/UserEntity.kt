package dev.freddiesilver.stocksim.entities

import dev.freddiesilver.stocksim.user.Balance
import dev.freddiesilver.stocksim.user.Email
import dev.freddiesilver.stocksim.user.PasswordValidationInfo
import dev.freddiesilver.stocksim.user.User
import dev.freddiesilver.stocksim.user.Username
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false, length = 20)
    val username: String,
    @Column(unique = true, nullable = false, length = 255)
    val email: String,
    @Column(name = "password_validation_info", nullable = false, length = 255)
    val passwordValidationInfo: String,
    @Column(nullable = false, precision = 19, scale = 4)
    var balance: BigDecimal,
){
    fun toDomain(): User {
        return User(
            id = id,
            username = Username(username),
            email = Email(email),
            passwordValidationInfo = PasswordValidationInfo(passwordValidationInfo),
            balance = Balance(balance),
            )
    }
}
