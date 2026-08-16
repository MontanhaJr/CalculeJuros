package com.montanhajr.calculejuros.core.domain.usecase

import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.data.repository.SimulationRepository
import com.montanhajr.calculejuros.core.domain.model.SimulationInput
import com.montanhajr.calculejuros.core.domain.model.SimulationResult
import javax.inject.Inject

class SaveSimulationUseCase @Inject constructor(
    private val repository: SimulationRepository
) {
    suspend operator fun invoke(
        input: SimulationInput,
        result: SimulationResult,
        scenarioName: String? = null,
        isFavorite: Boolean = false,
        iconType: String = "default",
        id: Long = 0
    ): Long {
        val entity = SimulationEntity(
            id = id,
            date = System.currentTimeMillis(),
            productName = scenarioName ?: "Simulação",
            
            // Inputs
            inputProductPrice = input.productPrice,
            inputDiscountPercentage = input.discountPercentage,
            inputCashPrice = input.cashPrice,
            inputUseDiscountToggle = input.useDiscountToggle,
            inputInstallmentsCount = input.installmentsCount,
            inputMonthlyCardRate = input.monthlyCardRate,
            inputTotalInstallmentValue = input.totalInstallmentValue,
            inputUseMonthlyRateToggle = input.useMonthlyRateToggle,
            inputAnnualProfitability = input.annualProfitability,
            inputMonthlyProfitability = input.monthlyProfitability,
            inputUseAnnualProfitabilityToggle = input.useAnnualProfitabilityToggle,
            
            // Results
            productPrice = result.productPrice,
            cashPrice = result.cashPrice,
            installmentsCount = result.installmentsCount,
            installmentValue = result.installmentValue,
            winner = result.recommendation.name,
            difference = result.difference,
            
            // UI
            isFavorite = isFavorite,
            scenarioName = scenarioName,
            iconType = iconType
        )
        return repository.insertSimulation(entity)
    }
}
