package com.montanhajr.calculejuros.feature.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HistoryItem(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val resultType: String, // "Parcelar" or "À vista"
    val resultLabel: String, // "Você ganha" or "Você economiza"
    val resultValue: String,
    val iconType: String // "laptop", "phone", "tv", "watch", "ps5"
)

data class HistoryUiState(
    val totalSimulations: String = "24",
    val totalSimulationsPeriod: String = "este mês",
    val potentialGain: String = "R$ 1.248,50",
    val potentialGainLabel: String = "no total",
    val averageGain: String = "R$ 52,02",
    val averageGainLabel: String = "de vantagem",
    val recentSimulations: List<HistoryItem> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState(
        recentSimulations = listOf(
            HistoryItem(
                id = "1",
                title = "Notebook Dell i5",
                description = "12x no cartão • 10% à vista",
                timestamp = "Hoje, 10:30",
                resultType = "Parcelar",
                resultLabel = "Você ganha",
                resultValue = "R$ 184,32",
                iconType = "laptop"
            ),
            HistoryItem(
                id = "2",
                title = "iPhone 15",
                description = "10x no cartão • 5% à vista",
                timestamp = "Ontem, 14:22",
                resultType = "À vista",
                resultLabel = "Você economiza",
                resultValue = "R$ 218,75",
                iconType = "phone"
            ),
            HistoryItem(
                id = "3",
                title = "Smart TV 55\"",
                description = "8x no cartão • 8% à vista",
                timestamp = "18/05/2024, 09:15",
                resultType = "Parcelar",
                resultLabel = "Você ganha",
                resultValue = "R$ 96,80",
                iconType = "tv"
            ),
            HistoryItem(
                id = "4",
                title = "Apple Watch Series 9",
                description = "6x no cartão • 0% à vista",
                timestamp = "15/05/2024, 16:40",
                resultType = "À vista",
                resultLabel = "Você economiza",
                resultValue = "R$ 75,40",
                iconType = "watch"
            ),
            HistoryItem(
                id = "5",
                title = "PlayStation 5",
                description = "12x no cartão • 3% à vista",
                timestamp = "12/05/2024, 11:08",
                resultType = "Parcelar",
                resultLabel = "Você ganha",
                resultValue = "R$ 142,10",
                iconType = "ps5"
            )
        )
    ))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
}
