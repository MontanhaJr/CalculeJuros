package com.montanhajr.calculejuros.feature.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.data.repository.CurrencyPreferencesRepository
import com.montanhajr.calculejuros.core.data.repository.UserPreferencesRepository
import com.montanhajr.calculejuros.core.domain.usecase.DeleteSimulationUseCase
import com.montanhajr.calculejuros.core.domain.usecase.GetFavoritesUseCase
import com.montanhajr.calculejuros.core.domain.usecase.GetHistoryUseCase
import com.montanhajr.calculejuros.core.domain.usecase.ToggleFavoriteUseCase
import com.montanhajr.calculejuros.core.util.toCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class HistoryFilter {
    ALL, INSTALLMENTS, CASH
}

data class HistoryItem(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val resultType: String, // "Parcelar" or "À vista"
    val resultLabel: String, // "Você ganha" or "Você economiza"
    val resultValue: String,
    val iconType: String, // "laptop", "phone", "tv", "watch", "ps5"
    val isFavorite: Boolean,
    val isLocked: Boolean = false,
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
    val selectedSimulation: SimulationEntity? = null,
    val activeFilter: HistoryFilter = HistoryFilter.ALL,
    val pendingDelete: SimulationEntity? = null,
    val isPro: Boolean = false,
    val showProLimitAlert: Boolean = false
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getHistoryUseCase: GetHistoryUseCase,
    private val deleteSimulationUseCase: DeleteSimulationUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    currencyPreferencesRepository: CurrencyPreferencesRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectedSimulation = MutableStateFlow<SimulationEntity?>(null)
    private val _activeFilter = MutableStateFlow(HistoryFilter.ALL)
    private val _pendingDelete = MutableStateFlow<SimulationEntity?>(null)
    private val _showProLimitAlert = MutableStateFlow(false)

    private data class HistoryInternalState(
        val selected: SimulationEntity?,
        val filter: HistoryFilter,
        val pendingDelete: SimulationEntity?,
        val showProLimitAlert: Boolean
    )

    val uiState: StateFlow<HistoryUiState> = combine(
        getHistoryUseCase(),
        currencyPreferencesRepository.currencySymbol,
        userPreferencesRepository.isPro,
        combine(_selectedSimulation, _activeFilter, _pendingDelete, _showProLimitAlert) { sel, filt, pend, alert ->
            HistoryInternalState(sel, filt, pend, alert)
        }
    ) { simulations, currencySymbol, isPro, internal ->
        val sortedSimulations = simulations.sortedByDescending { it.date }

        val filteredSimulations = when (internal.filter) {
            HistoryFilter.ALL -> sortedSimulations
            HistoryFilter.INSTALLMENTS -> sortedSimulations.filter { it.winner == "PARCELADO" }
            HistoryFilter.CASH -> sortedSimulations.filter { it.winner == "A_VISTA" }
        }

        val totalGain = simulations.sumOf { it.difference }
        HistoryUiState(
            totalSimulations = simulations.size.toString(),
            totalSimulationsPeriod = context.getString(R.string.label_this_month),
            potentialGain = totalGain.toCurrency(currencySymbol),
            potentialGainLabel = context.getString(R.string.label_in_total),
            averageGain = if (simulations.isNotEmpty()) (totalGain / simulations.size).toCurrency(currencySymbol) else 0.0.toCurrency(currencySymbol),
            averageGainLabel = context.getString(R.string.label_advantage),
            recentSimulations = filteredSimulations.mapIndexed { index, entity ->
                HistoryItem(
                    id = entity.id.toString(),
                    title = entity.scenarioName ?: context.getString(R.string.default_simulation_name),
                    description = context.getString(R.string.label_installments_card, entity.inputInstallmentsCount),
                    timestamp = formatTimestamp(entity.date),
                    resultType = if (entity.winner == "PARCELADO") context.getString(R.string.winner_installments) else context.getString(R.string.winner_cash),
                    resultLabel = if (entity.winner == "PARCELADO") context.getString(R.string.label_you_gain) else context.getString(R.string.label_you_save),
                    resultValue = entity.difference.toCurrency(currencySymbol),
                    iconType = entity.iconType,
                    isFavorite = entity.isFavorite,
                    isLocked = !isPro && index >= 6,
                    fullEntity = entity
                )
            },
            selectedSimulation = internal.selected,
            activeFilter = internal.filter,
            pendingDelete = internal.pendingDelete,
            isPro = isPro,
            showProLimitAlert = internal.showProLimitAlert
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

    fun requestDelete(simulation: SimulationEntity) {
        _pendingDelete.value = simulation
    }

    fun confirmDelete() {
        val simulation = _pendingDelete.value ?: return
        viewModelScope.launch {
            deleteSimulationUseCase(simulation)
            _pendingDelete.value = null
        }
    }

    fun dismissDeleteDialog() {
        _pendingDelete.value = null
    }

    fun toggleFavorite(simulation: SimulationEntity) {
        viewModelScope.launch {
            if (!simulation.isFavorite) {
                val currentFavorites = getFavoritesUseCase().first().size
                if (!uiState.value.isPro && currentFavorites >= 3) {
                    _showProLimitAlert.value = true
                    return@launch
                }
            }
            toggleFavoriteUseCase(simulation)
        }
    }

    fun dismissProLimitAlert() {
        _showProLimitAlert.value = false
    }

    fun setFilter(filter: HistoryFilter) {
        _activeFilter.value = filter
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val now = Calendar.getInstance()
        val simDate = Calendar.getInstance().apply { time = date }
        
        return when {
            isSameDay(now, simDate) -> context.getString(R.string.label_today, SimpleDateFormat("HH:mm", Locale.getDefault()).format(date))
            isYesterday(now, simDate) -> context.getString(R.string.label_yesterday, SimpleDateFormat("HH:mm", Locale.getDefault()).format(date))
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
