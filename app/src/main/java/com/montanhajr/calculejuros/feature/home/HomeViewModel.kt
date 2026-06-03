package com.montanhajr.calculejuros.feature.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(
        recentSimulations = listOf(
            RecentSimulation(
                id = "1",
                title = "Notebook Dell i5",
                details = "12x no cartão • 10% à vista",
                yield = "Rentabilidade: 12% a.a.",
                value = "R$ 184,32",
                type = "Parcelar"
            ),
            RecentSimulation(
                id = "2",
                title = "iPhone 15",
                details = "10x no cartão • 5% à vista",
                yield = "Rentabilidade: 10% a.a.",
                value = "R$ 218,75",
                type = "À vista"
            )
        ),
        suggestedResult = RecentSimulation(
            id = "0",
            title = "Vale a pena PARCELAR!",
            details = "Você pode ganhar até",
            yield = "investindo o que deixaria de pagar à vista.",
            value = "R$ 184,32",
            type = "Parcelar"
        )
    ))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
