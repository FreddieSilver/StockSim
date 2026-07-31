package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.company.CompanyService
import dev.freddiesilver.stocksim.dto.company.CompanyCreateDto
import dev.freddiesilver.stocksim.dto.toDto
import dev.freddiesilver.stocksim.helpers.dataResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CompanyController(
    private val companyService: CompanyService,
) {
    @PostMapping("/companies")
    fun createCompany(
        @RequestBody input: CompanyCreateDto,
    ): ResponseEntity<*> {
        val result = companyService.createCompany(input.name, input.ticker, input.description, input.volatility, input.drift)
        return dataResponse(
            status = HttpStatus.CREATED,
            data = result.toDto()
        )
    }

    @GetMapping("/companies/{id}")
    fun getCompanyById(
        @PathVariable id: String,
    ): ResponseEntity<*> {
        val result = companyService.getCompanyById(id.toLong())
        return dataResponse(
            status = HttpStatus.OK,
            data = result.toDto()
        )
    }
}
