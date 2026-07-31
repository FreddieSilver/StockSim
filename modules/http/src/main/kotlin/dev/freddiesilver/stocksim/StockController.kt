package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.stock.StockCreateDto
import dev.freddiesilver.stocksim.dto.toDto
import dev.freddiesilver.stocksim.helpers.dataResponse
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
        return dataResponse(
            status = HttpStatus.CREATED,
            data = result.toDto()
        )
    }

    @GetMapping("/stocks/{id}")
    fun getStockById(
        @PathVariable id: String,
    ): ResponseEntity<*> {
        val result = stockService.getStockById(id.toLong())
        return dataResponse(
            status = HttpStatus.OK,
            data = result.toDto()
        )
    }

    @GetMapping("/stocks/{id}/history")
    fun getStockHistoryById(
        @PathVariable id: String
    ): ResponseEntity<*>{
        val result = stockService.getStockHistory(id.toLong(),200)
        return dataResponse(
            status = HttpStatus.OK,
            data = result.map { it.toDto() }
        )
    }

    @GetMapping("/stocks")
    fun getAllStocks(): ResponseEntity<*> {
        val result = stockService.getAllStocks()
        return dataResponse(
            status = HttpStatus.OK,
            data = result.map { it.toDto() }
        )
    }
}
