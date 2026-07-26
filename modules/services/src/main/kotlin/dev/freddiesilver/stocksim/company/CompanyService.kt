package dev.freddiesilver.stocksim.company

import dev.freddiesilver.stocksim.CompanyRepository
import dev.freddiesilver.stocksim.company.error.CompanyError
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class CompanyService(
    private val companyRepo: CompanyRepository,
) {
    fun createCompany(
        name: String,
        ticker: String,
        description: String,
        volatility: Double,
        drift: Double,
    ): Company =
        if (companyRepo.findByTicker(ticker) != null) {
            throw CompanyError.CompanyAlreadyExists()
        } else {
            companyRepo.createCompany(name, ticker, description, volatility, drift)
        }

    fun getCompanyById(id: Long): Company = companyRepo.findById(id) ?: throw CompanyError.CompanyNotFound()
}
