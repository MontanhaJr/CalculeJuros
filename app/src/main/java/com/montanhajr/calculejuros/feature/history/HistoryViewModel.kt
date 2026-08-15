package com.montanhajr.calculejuros.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HistoryItem(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val resultType: String, // "Parcelar" or "À vista"
    val resultLabel: String, // "Você ganha" or "Você economiza"
    val resultValue: String,
    val iconType: String, // "laptop", "phone", "tv", "watch", "ps5"
    val fullEntity: SimulationEntity
)

data class HistoryUiState(
    val totalSimulations: String = "0",
    val totalSimulationsPeriod: String = "este mês",
    val potentialGain: String = "R$ 0,00",
    val potentialGainLabel: String = "no total",
    val averageGain: String = "R$ 0,00",
    val averageGainLabel: String = "de vantagem",
    val recentSimulations: List<HistoryItem> = emptyList(),
    val selectedSimulation: SimulationEntity? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    private val _selectedSimulation = MutableStateFlow<SimulationEntity?>(null)

    val uiState: StateFlow<HistoryUiState> = combine(
        getHistoryUseCase(),
        _selectedSimulation
    ) { simulations, selected ->
        val totalGain = simulations.sumOf { it.difference }
        HistoryUiState(
            totalSimulations = simulations.size.toString(),
            potentialGain = "R$ ${String.format("%.2f", totalGain)}",
            averageGain = if (simulations.isNotEmpty()) "R$ ${String.format("%.2f", totalGain / simulations.size)}" else "R$ 0,00",
            recentSimulations = simulations.map { entity ->
                HistoryItem(
                    id = entity.id.toString(),
                    title = entity.scenarioName ?: "Simulação",
                    description = "${entity.inputInstallmentsCount}x no cartão",
                    timestamp = formatTimestamp(entity.date),
                    resultType = if (entity.winner == "PARCELADO") "Parcelar" else "À vista",
                    resultLabel = if (entity.winner == "PARCELADO") "Você ganha" else "Você economiza",
                    resultValue = "R$ ${String.format("%.2f", entity.difference)}",
                    iconType = entity.iconType,
                    fullEntity = entity
                )
            },
            selectedSimulation = selected
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun onSimulationClick(simulation: SimulationEntity) {
        _selectedSimulation.value = simulation
    }

    fun onDismissModal() {
        _selectedSimulation.value = null
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val now = Calendar.getInstance()
        val simDate = Calendar.getInstance().apply { time = date }
        
        return when {
            isSameDay(now, simDate) -> "Hoje, ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)}"
            isYesterday(now, simDate) -> "Ontem, ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)}"
            else -> SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(date)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(now: Calendar, simDate: Calendar): Boolean {
        val yesterday = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        return isSameDay(yesterday, simDate)
    }
}
