package com.montanhajr.calculejuros.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.domain.usecase.GetFavoritesUseCase
import com.montanhajr.calculejuros.core.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class FavoriteItem(
    val id: String,
    val title: String,
    val description: String,
    val creationDate: String,
    val resultType: String, // "Parcelar" or "À vista"
    val resultLabel: String, // "Você ganha" or "Você economiza"
    val resultValue: String,
    val iconType: String, // "laptop", "phone", "tv", "watch", "ps5"
    val fullEntity: SimulationEntity
)

data class FavoritesUiState(
    val favoriteSimulations: List<FavoriteItem> = emptyList(),
    val selectedSimulation: SimulationEntity? = null,
    val pendingUnfavorite: SimulationEntity? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _selectedSimulation = MutableStateFlow<SimulationEntity?>(null)
    private val _pendingUnfavorite = MutableStateFlow<SimulationEntity?>(null)

    val uiState: StateFlow<FavoritesUiState> = combine(
        getFavoritesUseCase(),
        _selectedSimulation,
        _pendingUnfavorite
    ) { simulations, selected, pending ->
        FavoritesUiState(
            favoriteSimulations = simulations.map { entity ->
                FavoriteItem(
                    id = entity.id.toString(),
                    title = entity.scenarioName ?: "Simulação",
                    description = "${entity.inputInstallmentsCount}x no cartão",
                    creationDate = "Criado em ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(entity.date))}",
                    resultType = if (entity.winner == "PARCELADO") "Parcelar" else "À vista",
                    resultLabel = if (entity.winner == "PARCELADO") "Você ganha" else "Você economiza",
                    resultValue = "R$ ${String.format("%.2f", entity.difference)}",
                    iconType = entity.iconType,
                    fullEntity = entity
                )
            },
            selectedSimulation = selected,
            pendingUnfavorite = pending
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesUiState()
    )

    fun onSimulationClick(simulation: SimulationEntity) {
        _selectedSimulation.value = simulation
    }

    fun onDismissModal() {
        _selectedSimulation.value = null
    }

    fun requestUnfavorite(simulation: SimulationEntity) {
        _pendingUnfavorite.value = simulation
    }

    fun confirmUnfavorite() {
        val simulation = _pendingUnfavorite.value ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(simulation)
            _pendingUnfavorite.value = null
        }
    }

    fun dismissUnfavoriteDialog() {
        _pendingUnfavorite.value = null
    }
}
