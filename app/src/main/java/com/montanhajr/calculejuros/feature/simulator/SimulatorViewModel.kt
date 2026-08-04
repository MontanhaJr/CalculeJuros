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
import kotlin.math.pow

@HiltViewModel
class SimulatorViewModel @Inject constructor(
    private val calculateSimulationUseCase: CalculateSimulationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    fun onProductPriceChange(value: String) {
        _uiState.update { it.copy(productPrice = formatToCurrency(value)) }
    }

    fun onDiscountChange(value: String) {
        val cleanInput = value.filter { it.isDigit() }
        val longValue = cleanInput.toLongOrNull() ?: 0L
        
        // Bloqueio em 100,00 % (representado por 10000 no input limpo)
        val finalInput = if (longValue > 10000) "10000" else cleanInput
        _uiState.update { it.copy(discountPercentage = formatToCurrency(finalInput)) }
    }

    fun onUseDiscountToggle(useDiscount: Boolean) {
        val state = _uiState.value
        val productPrice = state.productPrice.toBrazilDoubleOrNull() ?: 0.0
        
        if (useDiscount != state.useDiscount) {
            if (useDiscount) {
                // Switching to Discount %
                val cashPrice = state.cashPrice.toBrazilDoubleOrNull() ?: 0.0
                val perc = if (productPrice > 0) ((productPrice - cashPrice) / productPrice * 100.0).coerceIn(0.0, 100.0) else 0.0
                _uiState.update { it.copy(useDiscount = true, discountPercentage = formatDouble(perc)) }
            } else {
                // Switching to Cash Price
                val perc = (state.discountPercentage.toBrazilDoubleOrNull() ?: 0.0).coerceIn(0.0, 100.0)
                val cashPrice = productPrice * (1 - perc / 100.0)
                _uiState.update { it.copy(useDiscount = false, cashPrice = formatDouble(cashPrice)) }
            }
        }
    }

    fun onCashPriceChange(value: String) {
        _uiState.update { it.copy(cashPrice = formatToCurrency(value)) }
    }

    fun onInstallmentsChange(count: Int) {
        _uiState.update { it.copy(installmentsCount = count) }
    }

    fun onInstallmentsTextChange(value: String) {
        val count = value.filter { it.isDigit() }.toIntOrNull() ?: 0
        _uiState.update { it.copy(installmentsCount = count) }
    }

    fun onUseMonthlyRateToggle(useMonthlyRate: Boolean) {
        val state = _uiState.value
        val productPrice = state.productPrice.toBrazilDoubleOrNull() ?: 0.0
        val n = state.installmentsCount

        if (useMonthlyRate != state.useMonthlyRate) {
            if (useMonthlyRate) {
                // Switching to Monthly Rate
                val totalValue = state.totalInstallmentValue.toBrazilDoubleOrNull() ?: 0.0
                val installment = totalValue / n
                val i = calculateImplicitRate(productPrice, installment, n)
                _uiState.update { it.copy(useMonthlyRate = true, cardTaxRate = formatDouble(i * 100.0)) }
            } else {
                // Switching to Total Value
                val i = (state.cardTaxRate.toBrazilDoubleOrNull() ?: 0.0) / 100.0
                val installment = if (i == 0.0) productPrice / n else productPrice * i / (1 - (1 + i).pow(-n))
                val total = installment * n
                _uiState.update { it.copy(useMonthlyRate = false, totalInstallmentValue = formatDouble(total)) }
            }
        }
    }

    fun onTotalInstallmentValueChange(value: String) {
        _uiState.update { it.copy(totalInstallmentValue = formatToCurrency(value)) }
    }

    fun onCardTaxChange(value: String) {
        _uiState.update { it.copy(cardTaxRate = formatToCurrency(value)) }
    }

    fun onUseAnnualProfitabilityToggle(useAnnual: Boolean) {
        val state = _uiState.value
        if (useAnnual != state.useAnnualProfitability) {
            if (useAnnual) {
                // Switching to Annual Profitability
                val mensal = (state.investmentMonthlyRate.toBrazilDoubleOrNull() ?: 0.0) / 100.0
                val anual = (1 + mensal).pow(12) - 1
                _uiState.update { it.copy(useAnnualProfitability = true, investmentAnnualRate = formatDouble(anual * 100.0)) }
            } else {
                // Switching to Monthly Profitability
                val anual = (state.investmentAnnualRate.toBrazilDoubleOrNull() ?: 0.0) / 100.0
                val mensal = (1 + anual).pow(1.0 / 12.0) - 1
                _uiState.update { it.copy(useAnnualProfitability = false, investmentMonthlyRate = formatDouble(mensal * 100.0)) }
            }
        }
    }

    fun onInvestmentAnnualRateChange(value: String) {
        _uiState.update { it.copy(investmentAnnualRate = formatToCurrency(value)) }
    }

    fun onInvestmentMonthlyRateChange(value: String) {
        _uiState.update { it.copy(investmentMonthlyRate = formatToCurrency(value)) }
    }

    private fun formatToCurrency(input: String): String {
        val cleanInput = input.filter { it.isDigit() }
        if (cleanInput.isEmpty()) return "0,00"
        val longValue = cleanInput.toLong()
        val formatted = longValue.toString().padStart(3, '0')
        val integerPart = formatted.substring(0, formatted.length - 2)
        val decimalPart = formatted.substring(formatted.length - 2)
        return "$integerPart,$decimalPart"
    }

    private fun formatDouble(value: Double): String {
        return "%.2f".format(value).replace(".", ",")
    }

    private fun String.toBrazilDoubleOrNull(): Double? {
        return this.replace(",", ".").toDoubleOrNull()
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
            productPrice = state.productPrice.toBrazilDoubleOrNull() ?: 0.0,
            discountPercentage = state.discountPercentage.toBrazilDoubleOrNull() ?: 0.0,
            cashPrice = state.cashPrice.toBrazilDoubleOrNull() ?: 0.0,
            useDiscountToggle = state.useDiscount,
            installmentsCount = state.installmentsCount,
            monthlyCardRate = state.cardTaxRate.toBrazilDoubleOrNull() ?: 0.0,
            totalInstallmentValue = state.totalInstallmentValue.toBrazilDoubleOrNull() ?: 0.0,
            useMonthlyRateToggle = state.useMonthlyRate,
            annualProfitability = state.investmentAnnualRate.toBrazilDoubleOrNull() ?: 0.0,
            monthlyProfitability = state.investmentMonthlyRate.toBrazilDoubleOrNull() ?: 0.0,
            useAnnualProfitabilityToggle = state.useAnnualProfitability
        )
        
        val result = calculateSimulationUseCase(input)
        _uiState.update { it.copy(simulationResult = result) }
    }

    private fun calculateImplicitRate(principal: Double, installment: Double, n: Int): Double {
        if (installment * n <= principal) return 0.0
        var low = 0.0
        var high = 1.0
        fun principalParaTaxa(i: Double): Double {
            return if (i == 0.0) installment * n else installment * (1 - (1 + i).pow(-n)) / i
        }
        var iterations = 0
        while (principalParaTaxa(high) > principal && iterations < 100) {
            high *= 2.0
            iterations++
        }
        repeat(100) {
            val mid = (low + high) / 2.0
            if (principalParaTaxa(mid) > principal) low = mid else high = mid
        }
        return (low + high) / 2.0
    }
}
