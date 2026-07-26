package dev.freddiesilver.stocksim.company.error

sealed class CompanyError(
    override val message: String?,
) : Exception(message) {
    class CompanyNotFound : CompanyError("Company not found")

    class InvalidCompanyData(
        additionalMessage: String,
    ) : CompanyError("Invalid company data: $additionalMessage")

    class CompanyAlreadyExists : CompanyError("Company already exists")
}
