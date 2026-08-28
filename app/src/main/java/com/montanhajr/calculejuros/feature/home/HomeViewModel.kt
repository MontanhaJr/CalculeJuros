package com.montanhajr.calculejuros.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanhajr.calculejuros.core.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.Locale
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
    val suggestedResult: RecentSimulation? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getHistoryUseCase()
        .map { simulations ->
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
                    title = "", // Not used for suggested in UI anymore
                    installmentsCount = last.inputInstallmentsCount,
                    annualProfitability = last.inputAnnualProfitability,
                    difference = last.difference,
                    type = if (last.winner == "PARCELADO") "Parcelar" else "À vista",
                    isSuggested = true
                )
            }

            HomeUiState(
                recentSimulations = recentItems,
                suggestedResult = suggested
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )
}
