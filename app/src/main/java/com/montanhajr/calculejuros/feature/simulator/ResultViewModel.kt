package com.montanhajr.calculejuros.feature.simulator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanhajr.calculejuros.core.domain.model.SimulationInput
import com.montanhajr.calculejuros.core.domain.model.SimulationResult
import com.montanhajr.calculejuros.core.domain.usecase.CalculateSimulationUseCase
import com.montanhajr.calculejuros.core.domain.usecase.GetSimulationByIdUseCase
import com.montanhajr.calculejuros.core.domain.usecase.SaveSimulationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val result: SimulationResult? = null,
    val scenarioName: String? = null,
    val isFavorite: Boolean = false,
    val showSaveDialog: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getSimulationByIdUseCase: GetSimulationByIdUseCase,
    private val calculateSimulationUseCase: CalculateSimulationUseCase,
    private val saveSimulationUseCase: SaveSimulationUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    private var currentInput: SimulationInput? = null
    private var simulationId: Long = -1L

    init {
        simulationId = savedStateHandle.get<Long>("simulationId") ?: -1L
        if (simulationId != -1L) {
            loadSimulation(simulationId)
        }
    }

    private fun loadSimulation(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getSimulationByIdUseCase(id)?.let { entity ->
                val input = SimulationInput(
                    productPrice = entity.inputProductPrice,
                    discountPercentage = entity.inputDiscountPercentage,
                    cashPrice = entity.inputCashPrice,
                    useDiscountToggle = entity.inputUseDiscountToggle,
                    installmentsCount = entity.inputInstallmentsCount,
                    monthlyCardRate = entity.inputMonthlyCardRate,
                    totalInstallmentValue = entity.inputTotalInstallmentValue,
                    useMonthlyRateToggle = entity.inputUseMonthlyRateToggle,
                    annualProfitability = entity.inputAnnualProfitability,
                    monthlyProfitability = entity.inputMonthlyProfitability,
                    useAnnualProfitabilityToggle = entity.inputUseAnnualProfitabilityToggle
                )
                currentInput = input
                val result = calculateSimulationUseCase(input)
                _uiState.update { it.copy(
                    result = result,
                    scenarioName = entity.scenarioName,
                    isFavorite = entity.isFavorite,
                    isLoading = false
                ) }
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onFavoriteClick() {
        val state = _uiState.value
        if (state.isFavorite) {
            val input = currentInput ?: return
            val result = state.result ?: return
            viewModelScope.launch {
                saveSimulationUseCase(
                    input = input,
                    result = result,
                    scenarioName = null,
                    isFavorite = false,
                    id = simulationId
                )
                _uiState.update { it.copy(isFavorite = false, scenarioName = "") }
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
        val input = currentInput ?: return
        val result = state.result ?: return

        viewModelScope.launch {
            saveSimulationUseCase(
                input = input,
                result = result,
                scenarioName = name.ifBlank { null },
                isFavorite = true,
                id = simulationId
            )
            _uiState.update { it.copy(
                showSaveDialog = false,
                isFavorite = true,
                scenarioName = name
            ) }
        }
    }
}
