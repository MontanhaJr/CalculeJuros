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
    val recommendation: RecommendationType
)

enum class RecommendationType {
    A_VISTA,
    PARCELADO,
    EMPATE
}

data class MonthlyDetail(
    val month: Int,
    val balance: Double,
    val installment: Double,
    val yield: Double
)
