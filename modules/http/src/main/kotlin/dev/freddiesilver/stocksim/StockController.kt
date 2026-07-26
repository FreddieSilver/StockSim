package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.StockCreateDto
import dev.freddiesilver.stocksim.stock.StockService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class StockController(
    private val stockService: StockService,
) {
    @PostMapping("/stocks")
    fun createStock(
        @RequestBody input: StockCreateDto,
    ): ResponseEntity<*> {
        val result = stockService.createStock(input.companyId, input.initialPrice)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result)
    }

    @GetMapping("/stocks/{id}")
    fun getStockById(
        @PathVariable id: String,
    ): ResponseEntity<*> {
        val result = stockService.getStockById(id.toLong())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result)
    }
}
