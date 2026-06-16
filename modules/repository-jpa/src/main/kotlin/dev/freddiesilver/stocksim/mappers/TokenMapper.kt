package dev.freddiesilver.stocksim.mappers

import dev.freddiesilver.stocksim.entities.TokenEntity
import dev.freddiesilver.stocksim.user.auth.token.Token
import dev.freddiesilver.stocksim.user.auth.token.TokenValidationInfo

object TokenMapper {
    fun toDomain(entity: TokenEntity): Token =
        Token(
            tokenValidationInfo = TokenValidationInfo(entity.tokenValidationInfo),
            userId = entity.userId,
            createdAt = entity.createdAt,
            lastUsedAt = entity.lastUsedAt,
        )

    fun toEntity(domain: Token): TokenEntity =
        TokenEntity(
            tokenValidationInfo = domain.tokenValidationInfo.validationInfo,
            userId = domain.userId,
            createdAt = domain.createdAt,
            lastUsedAt = domain.lastUsedAt,
        )
}
