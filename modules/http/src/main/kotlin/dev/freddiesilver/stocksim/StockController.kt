package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.dto.StockCreateDto
import dev.freddiesilver.stocksim.dto.stock.PricePointDto
import dev.freddiesilver.stocksim.stock.StockService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
class StockController(
    private val stockService: StockService,
) {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault())

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
            .status(HttpStatus.OK)
            .body(result)
    }

    @GetMapping("/stocks/{id}/history")
    fun getStockHistoryById(
        @PathVariable id: String
    ): ResponseEntity<*>{
        val result = stockService.getStockHistory(id.toLong())
        return ResponseEntity.status(HttpStatus.OK).body(result.map{ pricePoint -> 
            PricePointDto(
                time = timeFormatter.format(pricePoint.timestamp),
                price = pricePoint.price.value.toDouble()
            )
        })
    }

    @GetMapping("/stocks")
    fun getAllStocks(): ResponseEntity<*> {
        val result = stockService.getAllStocks()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result)
    }
}
