package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.StockCreateDto
import dev.freddiesilver.stocksim.helpers.errorResponse
import dev.freddiesilver.stocksim.stock.StockService
import dev.freddiesilver.stocksim.stock.error.StockError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class StockController(
    private val stockService: StockService
) {
    @PostMapping("/stocks")
    fun createStock(@RequestBody input: StockCreateDto): ResponseEntity<*> {
        return when (val result = stockService.createStock(input.companyId, input.initialPrice)) {
            is Success ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result)

            is Failure ->
                when (val error = result.value) {
                    is StockError.CompanyNotFound -> errorResponse(error.message, HttpStatus.NOT_FOUND)
                    is StockError.InvalidStockData -> errorResponse(error.message, HttpStatus.BAD_REQUEST)
                    else -> errorResponse("Company creation failed", HttpStatus.BAD_REQUEST)
                }
        }
    }

    @GetMapping("/stocks/{id}")
    fun getStockById(@PathVariable id: String): ResponseEntity<*> {
        return when (val result = stockService.getStockById(id.toLong())) {
            is Success ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result)

            is Failure ->
                when (val error = result.value) {
                    is StockError.StockNotFound -> errorResponse(error.message, HttpStatus.NOT_FOUND)
                    else -> errorResponse("Could not get company", HttpStatus.BAD_REQUEST)
                }
        }
    }

}