package com.montanhajr.calculejuros.core.domain.model

data class SimulationInput(
    val productPrice: Double,
    val discountPercentage: Double = 0.0,
    val cashPrice: Double = 0.0,
    val useDiscountToggle: Boolean = true,
    val installmentsCount: Int = 1,
    val monthlyCardRate: Double = 0.0,
    val totalInstallmentValue: Double = 0.0,
    val useMonthlyRateToggle: Boolean = true,
    val annualProfitability: Double = 0.0,
    val monthlyProfitability: Double = 0.0,
    val useAnnualProfitabilityToggle: Boolean = true
)
