package com.montanhajr.calculejuros.core.domain.model

data class SimulationInput(
    val productPrice: Double,
    val discountPercentage: Double = 0.0,
    val cashPrice: Double,
    val installmentsCount: Int = 1,
    val installmentValue: Double,
    val downPayment: Double = 0.0,
    val investmentType: InvestmentType = InvestmentType.NONE,
    val annualInvestmentRate: Double = 0.0,
    val hasProgressiveTax: Boolean = true
)
