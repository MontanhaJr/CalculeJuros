package com.montanhajr.calculejuros.core.domain.model

data class SimulationResult(
    val productPrice: Double,
    val cashPrice: Double,
    val discountValue: Double,
    val discountPercentage: Double,
    val installmentsCount: Int,
    val installmentValue: Double,
    val totalInstallmentValue: Double,
    val monthlyCardRate: Double,
    val annualCardRate: Double,
    val totalFinancingInterest: Double,
    val monthlyProfitability: Double,
    val annualProfitability: Double,
    val netGainCash: Double,
    val interestGainedCash: Double,
    val netGainInstallment: Double,
    val difference: Double,
    val recommendation: RecommendationType,
    val downPayment: Double = 0.0,
    val prepaymentDiscountValue: Double = 0.0,
    val monthlyDetails: List<MonthlyDetail> = emptyList()
)

enum class RecommendationType {
    A_VISTA,
    PARCELADO,
    EMPATE
}

data class MonthlyDetail(
    val month: Int,
    val installmentBalance: Double,
    val cashBalance: Double,
    val installmentPaid: Double,
    val yieldInstallment: Double,
    val yieldCash: Double
)
