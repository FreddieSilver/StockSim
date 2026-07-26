package dev.freddiesilver.stocksim.company

import dev.freddiesilver.stocksim.CompanyRepository
import dev.freddiesilver.stocksim.Either
import dev.freddiesilver.stocksim.company.error.CompanyError
import dev.freddiesilver.stocksim.failure
import dev.freddiesilver.stocksim.success

class CompanyService(
    private val companyRepo: CompanyRepository,
) {

    fun createCompany(
        name: String,
        ticker: String,
        description: String,
        volatility: Double,
        drift: Double
    ): Either<CompanyError, Company> =
            if (companyRepo.findByTicker(ticker) != null)
                failure(CompanyError.CompanyAlreadyExists())
            else success(companyRepo.createCompany(name, ticker, description, volatility, drift))


    fun getCompanyById(id:Long):Either<CompanyError, Company> {
        val company = companyRepo.findById(id)
        return if (company != null) {
            success(company)
        } else {
            failure(CompanyError.CompanyNotFound())
        }
    }
}