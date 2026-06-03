package com.montanhajr.calculejuros.core.domain.model

data class SimulationResult(
    val cashTotalCost: Double,
    val installmentTotalCost: Double,
    val difference: Double,
    val winner: WinnerType,
    val grossYield: Double,
    val taxAmount: Double,
    val netYield: Double,
    val monthlyDetails: List<MonthlyDetail>
)

enum class WinnerType {
    CASH,
    INSTALLMENT,
    NEUTRAL
}

data class MonthlyDetail(
    val month: Int,
    val balance: Double,
    val installment: Double,
    val yield: Double
)
