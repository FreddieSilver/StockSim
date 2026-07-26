package dev.freddiesilver.stocksim

import dev.freddiesilver.stocksim.company.CompanyService
import dev.freddiesilver.stocksim.company.error.CompanyError
import dev.freddiesilver.stocksim.dto.company.input.CompanyCreateDto
import dev.freddiesilver.stocksim.helpers.errorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CompanyController(
    private val companyService: CompanyService
) {
    @PostMapping("/companies")
    fun createCompany(@RequestBody input: CompanyCreateDto): ResponseEntity<*> {
        return when (val result = companyService.createCompany(input.name,input.ticker, input.description, input.volatility, input.drift)) {
            is Success ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result)

            is Failure ->
                when (val error = result.value) {
                    is CompanyError.CompanyAlreadyExists -> errorResponse(error.message, HttpStatus.BAD_REQUEST)
                    else -> errorResponse("Company creation failed", HttpStatus.BAD_REQUEST)
                }
        }
    }

    @GetMapping("/companies/{id}")
    fun getCompanyById(@PathVariable id: String): ResponseEntity<*> {
        return when (val result = companyService.getCompanyById(id.toLong())) {
            is Success ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result)

            is Failure ->
                when (val error = result.value) {
                    is CompanyError.CompanyNotFound -> errorResponse(error.message, HttpStatus.NOT_FOUND)
                    else -> errorResponse("Could not get company", HttpStatus.BAD_REQUEST)
                }
        }
    }

}