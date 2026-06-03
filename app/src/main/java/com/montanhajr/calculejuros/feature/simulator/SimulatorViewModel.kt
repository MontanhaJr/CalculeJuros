package com.montanhajr.calculejuros.feature.simulator

import androidx.lifecycle.ViewModel
import com.montanhajr.calculejuros.core.domain.model.InvestmentType
import com.montanhajr.calculejuros.core.domain.model.SimulationInput
import com.montanhajr.calculejuros.core.domain.usecase.CalculateSimulationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SimulatorViewModel @Inject constructor(
    private val calculateSimulationUseCase: CalculateSimulationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    fun onProductPriceChange(value: String) {
        _uiState.update { it.copy(productPrice = value, cashPrice = calculateCashPrice(value, it.discountPercentage)) }
    }

    fun onDiscountChange(value: String) {
        _uiState.update { it.copy(discountPercentage = value, cashPrice = calculateCashPrice(it.productPrice, value)) }
    }

    fun onCashPriceChange(value: String) {
        _uiState.update { it.copy(cashPrice = value, discountPercentage = calculateDiscountPercentage(it.productPrice, value)) }
    }

    fun onInstallmentsChange(count: Int) {
        _uiState.update { it.copy(installmentsCount = count) }
    }

    fun onInstallmentValueChange(value: String) {
        _uiState.update { it.copy(installmentValue = value) }
    }

    fun onInvestmentTypeChange(type: InvestmentType) {
        _uiState.update { it.copy(investmentType = type) }
    }

    fun onCardTaxChange(value: String) {
        _uiState.update { it.copy(cardTaxRate = value) }
    }

    fun onInvestmentRateChange(value: Float) {
        _uiState.update { it.copy(investmentAnnualRate = value) }
    }

    fun toggleHowItWorks() {
        _uiState.update { it.copy(isHowItWorksExpanded = !it.isHowItWorksExpanded) }
    }

    fun onSaveScenarioToggle(enabled: Boolean) {
        _uiState.update { it.copy(isSaveScenarioEnabled = enabled) }
    }

    fun onClearFields() {
        _uiState.value = SimulatorUiState()
    }

    fun onCalculate() {
        val state = _uiState.value
        val input = SimulationInput(
            productPrice = state.productPrice.toDoubleOrNull() ?: 0.0,
            cashPrice = state.cashPrice.toDoubleOrNull() ?: 0.0,
            installmentsCount = state.installmentsCount,
            installmentValue = state.installmentValue.toDoubleOrNull() ?: 0.0,
            downPayment = state.downPayment.toDoubleOrNull() ?: 0.0,
            investmentType = state.investmentType,
            annualInvestmentRate = state.investmentAnnualRate.toDouble() / 100.0
        )
        
        val result = calculateSimulationUseCase(input)
        _uiState.update { it.copy(simulationResult = result) }
    }

    private fun calculateCashPrice(price: String, discount: String): String {
        val p = price.toDoubleOrNull() ?: return ""
        val d = discount.toDoubleOrNull() ?: 0.0
        return (p * (1 - d / 100)).toString()
    }

    private fun calculateDiscountPercentage(price: String, cashPrice: String): String {
        val p = price.toDoubleOrNull() ?: return "0"
        val cp = cashPrice.toDoubleOrNull() ?: return "0"
        if (p == 0.0) return "0"
        return ((1 - cp / p) * 100).toString()
    }

    private fun getAnnualRate(type: InvestmentType, custom: Double): Double {
        return when (type) {
            InvestmentType.NONE -> 0.0
            InvestmentType.SAVINGS -> 0.06 // 6%
            InvestmentType.TREASURY_SELIC -> 0.11 // 11%
            InvestmentType.CDB_BIG_BANK -> 0.09 // 9%
            InvestmentType.CDB_DIGITAL_BANK -> 0.105 // 10.5%
            InvestmentType.CUSTOM -> custom / 100
        }
    }
}
