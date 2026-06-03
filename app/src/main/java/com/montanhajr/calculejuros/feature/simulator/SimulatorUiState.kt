package com.montanhajr.calculejuros.feature.simulator

import com.montanhajr.calculejuros.core.domain.model.InvestmentType
import com.montanhajr.calculejuros.core.domain.model.SimulationResult

data class SimulatorUiState(
    val productPrice: String = "",
    val discountPercentage: String = "0",
    val cashPrice: String = "",
    val installmentsCount: Int = 12,
    val installmentValue: String = "",
    val cardTaxRate: String = "4,99",
    val investmentAnnualRate: Float = 12f,
    val isHowItWorksExpanded: Boolean = false,
    val isSaveScenarioEnabled: Boolean = false,
    val downPayment: String = "0",
    val investmentType: InvestmentType = InvestmentType.SAVINGS,
    val customRate: String = "10",
    val isAdvancedExpanded: Boolean = false,
    val simulationResult: SimulationResult? = null,
    val isLoading: Boolean = false
)
