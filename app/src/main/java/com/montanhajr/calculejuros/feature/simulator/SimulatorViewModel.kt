package com.montanhajr.calculejuros.feature.simulator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.data.repository.AdsPreferencesRepository
import com.montanhajr.calculejuros.core.data.repository.CurrencyPreferencesRepository
import com.montanhajr.calculejuros.core.domain.model.InvestmentType
import com.montanhajr.calculejuros.core.domain.model.SimulationInput
import com.montanhajr.calculejuros.core.domain.usecase.CalculateSimulationUseCase
import com.montanhajr.calculejuros.core.domain.usecase.GetSimulationByIdUseCase
import com.montanhajr.calculejuros.core.domain.usecase.SaveSimulationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class SimulatorViewModel @Inject constructor(
    private val calculateSimulationUseCase: CalculateSimulationUseCase,
    private val saveSimulationUseCase: SaveSimulationUseCase,
    private val getSimulationByIdUseCase: GetSimulationByIdUseCase,
    private val currencyPreferencesRepository: CurrencyPreferencesRepository,
    private val adsPreferencesRepository: AdsPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    private val _showRewardedAdEvent = MutableSharedFlow<Unit>()
    val showRewardedAdEvent: SharedFlow<Unit> = _showRewardedAdEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            currencyPreferencesRepository.currencySymbol.collect { symbol ->
                _uiState.update { it.copy(currencySymbol = symbol) }
            }
        }

        viewModelScope.launch {
            combine(
                adsPreferencesRepository.showAds,
                adsPreferencesRepository.skipNextRewardedAd
            ) { showAds, skip ->
                if (!showAds) {
                    _uiState.update { it.copy(shouldShowRewardedAdIcon = false) }
                } else {
                    _uiState.update { it.copy(shouldShowRewardedAdIcon = !skip) }
                }
            }.collect()
        }

        savedStateHandle.getStateFlow<Long>("simulationId", -1L)
            .onEach { id ->
                if (id != -1L) {
                    loadSimulation(id)
                }
            }
            .launchIn(viewModelScope)
    }

    fun setCurrencySymbol(symbol: String) {
        viewModelScope.launch {
            currencyPreferencesRepository.saveCurrencySymbol(symbol)
        }
    }

    private fun loadSimulation(id: Long) {
        viewModelScope.launch {
            getSimulationByIdUseCase(id)?.let { entity ->
                _uiState.update { it.copy(
                    productPrice = fromDoubleToDigits(entity.inputProductPrice),
                    discountPercentage = fromDoubleToDigits(entity.inputDiscountPercentage),
                    useDiscount = entity.inputUseDiscountToggle,
                    cashPrice = fromDoubleToDigits(entity.inputCashPrice),
                    installmentsCount = entity.inputInstallmentsCount,
                    cardTaxRate = fromDoubleToDigits(entity.inputMonthlyCardRate),
                    useMonthlyRate = entity.inputUseMonthlyRateToggle,
                    totalInstallmentValue = fromDoubleToDigits(entity.inputTotalInstallmentValue),
                    investmentAnnualRate = fromDoubleToDigits(entity.inputAnnualProfitability),
                    investmentMonthlyRate = fromDoubleToDigits(entity.inputMonthlyProfitability),
                    useAnnualProfitability = entity.inputUseAnnualProfitabilityToggle,
                    useDownPayment = entity.inputUseDownPayment,
                    downPayment = fromDoubleToDigits(entity.inputDownPayment),
                    downPaymentPercentage = fromDoubleToDigits(entity.inputDownPaymentPercentage),
                    downPaymentIsPercentage = entity.inputDownPaymentIsPercentage,
                    usePrepaymentDiscount = entity.inputUsePrepaymentDiscount,
                    prepaymentDiscountPercentage = fromDoubleToDigits(entity.inputPrepaymentDiscountPercentage),
                    prepaymentDiscountValue = fromDoubleToDigits(entity.inputPrepaymentDiscountValue),
                    prepaymentDiscountIsPercentage = entity.inputPrepaymentDiscountIsPercentage,
                    prepaidInstallmentsCount = entity.inputPrepaidInstallmentsCount.toString(),
                    isSavedAsFavorite = entity.isFavorite,
                    scenarioName = entity.scenarioName ?: "",
                    simulationResult = null, // Do not show previous results
                    currentSimulationId = entity.id
                ) }
            }
        }
    }

    fun onProductPriceChange(value: String) {
        _uiState.update { it.copy(productPrice = value) }
    }

    fun onDiscountChange(value: String) {
        val longValue = value.toLongOrNull() ?: 0L
        // Bloqueio em 100,00 % (representado por 10000 no input limpo)
        val finalValue = if (longValue > 10000) "10000" else value
        _uiState.update { it.copy(discountPercentage = finalValue) }
    }

    fun onUseDiscountToggle(useDiscount: Boolean) {
        val state = _uiState.value
        val productPrice = state.productPrice.toBrazilDouble()
        
        if (useDiscount != state.useDiscount) {
            if (useDiscount) {
                // Switching to Discount %
                val cashPrice = state.cashPrice.toBrazilDouble()
                val perc = if (productPrice > 0) ((productPrice - cashPrice) / productPrice * 100.0).coerceIn(0.0, 100.0) else 0.0
                _uiState.update { it.copy(useDiscount = true, discountPercentage = fromDoubleToDigits(perc)) }
            } else {
                // Switching to Cash Price
                val perc = (state.discountPercentage.toBrazilDouble()).coerceIn(0.0, 100.0)
                val cashPrice = productPrice * (1 - perc / 100.0)
                _uiState.update { it.copy(useDiscount = false, cashPrice = fromDoubleToDigits(cashPrice)) }
            }
        }
    }

    fun onCashPriceChange(value: String) {
        _uiState.update { it.copy(cashPrice = value) }
    }

    fun onInstallmentsChange(count: Int) {
        val finalCount = count.coerceIn(1, 1000)
        _uiState.update { state ->
            val prepaidCount = state.prepaidInstallmentsCount.toIntOrNull() ?: 0
            val maxPrepaid = maxOf(0, finalCount - 1)
            val finalPrepaid = if (prepaidCount > maxPrepaid) maxPrepaid.toString() else state.prepaidInstallmentsCount
            state.copy(
                installmentsCount = finalCount,
                prepaidInstallmentsCount = finalPrepaid
            )
        }
    }

    fun onInstallmentsTextChange(value: String) {
        val count = value.filter { it.isDigit() }.toIntOrNull() ?: 0
        val finalCount = count.coerceIn(1, 1000)
        _uiState.update { state ->
            val prepaidCount = state.prepaidInstallmentsCount.toIntOrNull() ?: 0
            val maxPrepaid = maxOf(0, finalCount - 1)
            val finalPrepaid = if (prepaidCount > maxPrepaid) maxPrepaid.toString() else state.prepaidInstallmentsCount
            state.copy(
                installmentsCount = finalCount,
                prepaidInstallmentsCount = finalPrepaid
            )
        }
    }

    fun onUseMonthlyRateToggle(useMonthlyRate: Boolean) {
        val state = _uiState.value
        val productPrice = state.productPrice.toBrazilDouble()
        val n = state.installmentsCount

        if (useMonthlyRate != state.useMonthlyRate) {
            if (useMonthlyRate) {
                // Switching to Monthly Rate
                val totalValue = state.totalInstallmentValue.toBrazilDouble()
                val installment = totalValue / n
                val i = calculateImplicitRate(productPrice, installment, n)
                _uiState.update { it.copy(useMonthlyRate = true, cardTaxRate = fromDoubleToDigits(i * 100.0)) }
            } else {
                // Switching to Total Value
                val i = (state.cardTaxRate.toBrazilDouble()) / 100.0
                val installment = if (i == 0.0) productPrice / n else productPrice * i / (1 - (1 + i).pow(-n))
                val total = installment * n
                _uiState.update { it.copy(useMonthlyRate = false, totalInstallmentValue = fromDoubleToDigits(total)) }
            }
        }
    }

    fun onTotalInstallmentValueChange(value: String) {
        _uiState.update { it.copy(totalInstallmentValue = value) }
    }

    fun onCardTaxChange(value: String) {
        _uiState.update { it.copy(cardTaxRate = value) }
    }

    fun onUseAnnualProfitabilityToggle(useAnnual: Boolean) {
        val state = _uiState.value
        if (useAnnual != state.useAnnualProfitability) {
            if (useAnnual) {
                // Switching to Annual Profitability
                val mensal = (state.investmentMonthlyRate.toBrazilDouble()) / 100.0
                val anual = (1 + mensal).pow(12) - 1
                _uiState.update { it.copy(useAnnualProfitability = true, investmentAnnualRate = fromDoubleToDigits(anual * 100.0)) }
            } else {
                // Switching to Monthly Profitability
                val anual = (state.investmentAnnualRate.toBrazilDouble()) / 100.0
                val mensal = (1 + anual).pow(1.0 / 12.0) - 1
                _uiState.update { it.copy(useAnnualProfitability = false, investmentMonthlyRate = fromDoubleToDigits(mensal * 100.0)) }
            }
        }
    }

    fun onInvestmentAnnualRateChange(value: String) {
        _uiState.update { it.copy(investmentAnnualRate = value) }
    }

    fun onInvestmentMonthlyRateChange(value: String) {
        _uiState.update { it.copy(investmentMonthlyRate = value) }
    }

    fun onUseDownPaymentToggle(use: Boolean) {
        _uiState.update { it.copy(useDownPayment = use) }
    }

    fun onDownPaymentChange(value: String) {
        _uiState.update { it.copy(downPayment = value) }
    }

    fun onDownPaymentPercentageChange(value: String) {
        _uiState.update { it.copy(downPaymentPercentage = value) }
    }

    fun onDownPaymentTypeToggle(isPercentage: Boolean) {
        _uiState.update { it.copy(downPaymentIsPercentage = isPercentage) }
    }

    fun onUsePrepaymentDiscountToggle(use: Boolean) {
        _uiState.update { it.copy(usePrepaymentDiscount = use) }
    }

    fun onPrepaymentDiscountPercentageChange(value: String) {
        _uiState.update { it.copy(prepaymentDiscountPercentage = value) }
    }

    fun onPrepaymentDiscountValueChange(value: String) {
        _uiState.update { it.copy(prepaymentDiscountValue = value) }
    }

    fun onPrepaymentDiscountTypeToggle(isPercentage: Boolean) {
        _uiState.update { it.copy(prepaymentDiscountIsPercentage = isPercentage) }
    }

    fun onPrepaidInstallmentsChange(value: String) {
        val cleanValue = value.filter { it.isDigit() }
        if (cleanValue.isEmpty()) {
            _uiState.update { it.copy(prepaidInstallmentsCount = "") }
            return
        }
        val count = cleanValue.toIntOrNull() ?: 0
        val maxInstallments = _uiState.value.installmentsCount
        val maxPrepaid = maxOf(0, maxInstallments - 1)
        val finalCount = if (count > maxPrepaid) maxPrepaid else count
        _uiState.update { it.copy(prepaidInstallmentsCount = finalCount.toString()) }
    }

    private fun fromDoubleToDigits(value: Double): String {
        return kotlin.math.round(value * 100).toLong().toString()
    }

    private fun String.toBrazilDouble(): Double {
        val clean = this.filter { it.isDigit() }
        if (clean.isEmpty()) return 0.0
        return clean.toDouble() / 100.0
    }

    fun toggleHowItWorks() {
        _uiState.update { it.copy(isHowItWorksExpanded = !it.isHowItWorksExpanded) }
    }

    fun onClearFields() {
        _uiState.value = SimulatorUiState()
    }

    fun onFavoriteClick() {
        val state = _uiState.value
        if (state.isSavedAsFavorite) {
            // Unfavorite logic
            val result = state.simulationResult ?: return
            val id = state.currentSimulationId ?: return
            
            val input = SimulationInput(
                productPrice = state.productPrice.toBrazilDouble(),
                discountPercentage = state.discountPercentage.toBrazilDouble(),
                cashPrice = state.cashPrice.toBrazilDouble(),
                useDiscountToggle = state.useDiscount,
                installmentsCount = state.installmentsCount,
                monthlyCardRate = state.cardTaxRate.toBrazilDouble(),
                totalInstallmentValue = state.totalInstallmentValue.toBrazilDouble(),
                useMonthlyRateToggle = state.useMonthlyRate,
                annualProfitability = state.investmentAnnualRate.toBrazilDouble(),
                monthlyProfitability = state.investmentMonthlyRate.toBrazilDouble(),
                useAnnualProfitabilityToggle = state.useAnnualProfitability,
                downPayment = state.downPayment.toBrazilDouble(),
                downPaymentPercentage = state.downPaymentPercentage.toBrazilDouble(),
                downPaymentIsPercentage = state.downPaymentIsPercentage,
                useDownPayment = state.useDownPayment,
                prepaymentDiscountPercentage = state.prepaymentDiscountPercentage.toBrazilDouble(),
                prepaymentDiscountValue = state.prepaymentDiscountValue.toBrazilDouble(),
                usePrepaymentDiscount = state.usePrepaymentDiscount,
                prepaymentDiscountIsPercentage = state.prepaymentDiscountIsPercentage,
                prepaidInstallmentsCount = state.prepaidInstallmentsCount.toIntOrNull() ?: 0
            )

            viewModelScope.launch {
                saveSimulationUseCase(
                    input = input,
                    result = result,
                    scenarioName = null,
                    isFavorite = false,
                    id = id
                )
                _uiState.update { it.copy(
                    isSavedAsFavorite = false,
                    scenarioName = ""
                ) }
            }
        } else {
            _uiState.update { it.copy(showSaveDialog = true) }
        }
    }

    fun onDismissSaveDialog() {
        _uiState.update { it.copy(showSaveDialog = false) }
    }

    fun onConfirmSaveFavorite(name: String) {
        val state = _uiState.value
        val result = state.simulationResult ?: return
        
        val input = SimulationInput(
            productPrice = state.productPrice.toBrazilDouble(),
            discountPercentage = state.discountPercentage.toBrazilDouble(),
            cashPrice = state.cashPrice.toBrazilDouble(),
            useDiscountToggle = state.useDiscount,
            installmentsCount = state.installmentsCount,
            monthlyCardRate = state.cardTaxRate.toBrazilDouble(),
            totalInstallmentValue = state.totalInstallmentValue.toBrazilDouble(),
            useMonthlyRateToggle = state.useMonthlyRate,
            annualProfitability = state.investmentAnnualRate.toBrazilDouble(),
            monthlyProfitability = state.investmentMonthlyRate.toBrazilDouble(),
            useAnnualProfitabilityToggle = state.useAnnualProfitability,
            downPayment = state.downPayment.toBrazilDouble(),
            downPaymentPercentage = state.downPaymentPercentage.toBrazilDouble(),
            downPaymentIsPercentage = state.downPaymentIsPercentage,
            useDownPayment = state.useDownPayment,
            prepaymentDiscountPercentage = state.prepaymentDiscountPercentage.toBrazilDouble(),
            prepaymentDiscountValue = state.prepaymentDiscountValue.toBrazilDouble(),
            usePrepaymentDiscount = state.usePrepaymentDiscount,
            prepaymentDiscountIsPercentage = state.prepaymentDiscountIsPercentage,
            prepaidInstallmentsCount = state.prepaidInstallmentsCount.toIntOrNull() ?: 0
        )

        viewModelScope.launch {
            saveSimulationUseCase(
                input = input,
                result = result,
                scenarioName = name.ifBlank { null },
                isFavorite = true,
                id = state.currentSimulationId ?: 0L
            )
            _uiState.update { it.copy(
                showSaveDialog = false,
                isSavedAsFavorite = true,
                scenarioName = name
            ) }
        }
    }

    fun onCalculate() {
        if (_uiState.value.shouldShowRewardedAdIcon) {
            viewModelScope.launch {
                _showRewardedAdEvent.emit(Unit)
            }
        } else {
            onActualCalculate()
        }
    }

    fun onActualCalculate() {
        val state = _uiState.value
        
        val input = SimulationInput(
            productPrice = state.productPrice.toBrazilDouble(),
            discountPercentage = state.discountPercentage.toBrazilDouble(),
            cashPrice = state.cashPrice.toBrazilDouble(),
            useDiscountToggle = state.useDiscount,
            installmentsCount = state.installmentsCount,
            monthlyCardRate = state.cardTaxRate.toBrazilDouble(),
            totalInstallmentValue = state.totalInstallmentValue.toBrazilDouble(),
            useMonthlyRateToggle = state.useMonthlyRate,
            annualProfitability = state.investmentAnnualRate.toBrazilDouble(),
            monthlyProfitability = state.investmentMonthlyRate.toBrazilDouble(),
            useAnnualProfitabilityToggle = state.useAnnualProfitability,
            downPayment = state.downPayment.toBrazilDouble(),
            downPaymentPercentage = state.downPaymentPercentage.toBrazilDouble(),
            downPaymentIsPercentage = state.downPaymentIsPercentage,
            useDownPayment = state.useDownPayment,
            prepaymentDiscountPercentage = state.prepaymentDiscountPercentage.toBrazilDouble(),
            prepaymentDiscountValue = state.prepaymentDiscountValue.toBrazilDouble(),
            usePrepaymentDiscount = state.usePrepaymentDiscount,
            prepaymentDiscountIsPercentage = state.prepaymentDiscountIsPercentage,
            prepaidInstallmentsCount = state.prepaidInstallmentsCount.toIntOrNull() ?: 0
        )
        
        val result = calculateSimulationUseCase(input)
        _uiState.update { it.copy(simulationResult = result, isSavedAsFavorite = false) }

        // Save simulation automatically to history
        viewModelScope.launch {
            val id = saveSimulationUseCase(
                input = input,
                result = result,
                scenarioName = null,
                isFavorite = false
            )
            _uiState.update { it.copy(
                currentSimulationId = id,
                navigateToResultId = id
            ) }
        }
    }

    fun onNavigatedToResult() {
        _uiState.update { it.copy(navigateToResultId = null) }
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
