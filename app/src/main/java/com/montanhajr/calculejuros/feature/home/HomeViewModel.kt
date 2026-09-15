package com.montanhajr.calculejuros.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanhajr.calculejuros.core.data.repository.CurrencyPreferencesRepository
import com.montanhajr.calculejuros.core.data.repository.UserPreferencesRepository
import com.montanhajr.calculejuros.core.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecentSimulation(
    val id: String,
    val title: String,
    val installmentsCount: Int,
    val annualProfitability: Double,
    val difference: Double,
    val type: String, // "Parcelar" or "À vista"
    val isSuggested: Boolean = false
)

data class HomeUiState(
    val recentSimulations: List<RecentSimulation> = emptyList(),
    val suggestedResult: RecentSimulation? = null,
    val currencySymbol: String = "R$",
    val isPro: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHistoryUseCase: GetHistoryUseCase,
    private val currencyPreferencesRepository: CurrencyPreferencesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        getHistoryUseCase(),
        currencyPreferencesRepository.currencySymbol,
        userPreferencesRepository.isPro
    ) { simulations, currencySymbol, isPro ->
        val sortedSimulations = simulations.sortedByDescending { it.date }
        val recentItems = sortedSimulations.take(2).map { entity ->
            RecentSimulation(
                id = entity.id.toString(),
                title = entity.scenarioName ?: "Simulação",
                installmentsCount = entity.inputInstallmentsCount,
                annualProfitability = entity.inputAnnualProfitability,
                difference = entity.difference,
                type = if (entity.winner == "PARCELADO") "Parcelar" else "À vista"
            )
        }

        val suggested = sortedSimulations.firstOrNull()?.let { last ->
            RecentSimulation(
                id = last.id.toString(),
                title = "",
                installmentsCount = last.inputInstallmentsCount,
                annualProfitability = last.inputAnnualProfitability,
                difference = last.difference,
                type = if (last.winner == "PARCELADO") "Parcelar" else "À vista",
                isSuggested = true
            )
        }

        HomeUiState(
            recentSimulations = recentItems,
            suggestedResult = suggested,
            currencySymbol = currencySymbol,
            isPro = isPro
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun togglePro() {
        viewModelScope.launch {
            val current = userPreferencesRepository.isPro.first()
            userPreferencesRepository.setProStatus(!current)
        }
    }

    fun setCurrencySymbol(symbol: String) {
        viewModelScope.launch {
            currencyPreferencesRepository.saveCurrencySymbol(symbol)
        }
    }
}
