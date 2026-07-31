package dev.freddiesilver.stocksim.dto.holding

import dev.freddiesilver.stocksim.dto.stock.StockDto
import dev.freddiesilver.stocksim.dto.user.output.UserDto

data class HoldingDto(
    val user: UserDto,
    val stock: StockDto,
    val quantity: Double
)