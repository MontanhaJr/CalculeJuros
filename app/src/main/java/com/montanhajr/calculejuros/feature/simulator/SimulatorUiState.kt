package com.montanhajr.calculejuros.feature.simulator

import com.montanhajr.calculejuros.core.domain.model.InvestmentType
import com.montanhajr.calculejuros.core.domain.model.SimulationResult

data class SimulatorUiState(
    val productPrice: String = "",
    val discountPercentage: String = "0",
    val useDiscount: Boolean = true,
    val cashPrice: String = "",
    val installmentsCount: Int = 12,
    val cardTaxRate: String = "0",
    val useMonthlyRate: Boolean = true,
    val totalInstallmentValue: String = "",
    val investmentAnnualRate: String = "1200",
    val investmentMonthlyRate: String = "95",
    val useAnnualProfitability: Boolean = true,
    val isHowItWorksExpanded: Boolean = false,
    val isSaveScenarioEnabled: Boolean = false,
    val scenarioName: String = "",
    val simulationResult: SimulationResult? = null,
    val isLoading: Boolean = false,
    val showSaveDialog: Boolean = false,
    val isSavedAsFavorite: Boolean = false,
    val currentSimulationId: Long? = null
)
