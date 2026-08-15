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
    val details: String,
    val yield: String,
    val value: String,
    val type: String // "Parcelar" or "À vista"
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
                    details = "${entity.inputInstallmentsCount}x no cartão",
                    yield = "Rentabilidade: ${String.format(Locale.getDefault(), "%.1f", entity.inputAnnualProfitability)}% a.a.",
                    value = "R$ ${String.format(Locale.getDefault(), "%.2f", entity.difference)}",
                    type = if (entity.winner == "PARCELADO") "Parcelar" else "À vista"
                )
            }

            val suggested = sortedSimulations.firstOrNull()?.let { last ->
                RecentSimulation(
                    id = last.id.toString(),
                    title = "Vale a pena ${if (last.winner == "PARCELADO") "PARCELAR!" else "À VISTA!"}",
                    details = "Você pode ganhar até",
                    yield = "investindo o que deixaria de pagar à vista.",
                    value = "R$ ${String.format(Locale.getDefault(), "%.2f", last.difference)}",
                    type = if (last.winner == "PARCELADO") "Parcelar" else "À vista"
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
