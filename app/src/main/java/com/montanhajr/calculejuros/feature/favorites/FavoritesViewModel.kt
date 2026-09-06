package com.montanhajr.calculejuros.feature.favorites

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.data.repository.AdsPreferencesRepository
import com.montanhajr.calculejuros.core.data.repository.CurrencyPreferencesRepository
import com.montanhajr.calculejuros.core.data.repository.SimulationRepository
import com.montanhajr.calculejuros.core.domain.usecase.GetFavoritesUseCase
import com.montanhajr.calculejuros.core.domain.usecase.ToggleFavoriteUseCase
import com.montanhajr.calculejuros.core.util.toCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val pendingUnfavorite: SimulationEntity? = null,
    val isEditMode: Boolean = false
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val repository: SimulationRepository,
    currencyPreferencesRepository: CurrencyPreferencesRepository,
    private val adsPreferencesRepository: AdsPreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectedSimulation = MutableStateFlow<SimulationEntity?>(null)
    private val _pendingUnfavorite = MutableStateFlow<SimulationEntity?>(null)
    private val _isEditMode = MutableStateFlow(false)
    private val _reorderedList = MutableStateFlow<List<SimulationEntity>?>(null)

    val uiState: StateFlow<FavoritesUiState> = combine(
        getFavoritesUseCase(),
        currencyPreferencesRepository.currencySymbol,
        combine(_selectedSimulation, _pendingUnfavorite, _isEditMode) { sel, pen, edit -> Triple(sel, pen, edit) },
        _reorderedList
    ) { dbSimulations, currencySymbol, (selected, pending, isEdit), customList ->
        val dbIds = dbSimulations.map { it.id }.toSet()
        val simulations = if (customList != null) {
            val filteredCustom = customList.filter { it.id in dbIds }
            val missingFromCustom = dbSimulations.filter { dbItem -> customList.none { it.id == dbItem.id } }
            filteredCustom + missingFromCustom
        } else {
            dbSimulations
        }

        FavoritesUiState(
            favoriteSimulations = simulations.map { entity ->
                FavoriteItem(
                    id = entity.id.toString(),
                    title = entity.scenarioName ?: context.getString(R.string.default_simulation_name),
                    description = context.getString(R.string.label_installments_card, entity.inputInstallmentsCount),
                    creationDate = context.getString(
                        R.string.label_created_at,
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(entity.date))
                    ),
                    resultType = if (entity.winner == "PARCELADO") context.getString(R.string.winner_installments) else context.getString(R.string.winner_cash),
                    resultLabel = if (entity.winner == "PARCELADO") context.getString(R.string.label_you_gain) else context.getString(R.string.label_you_save),
                    resultValue = entity.difference.toCurrency(currencySymbol),
                    iconType = entity.iconType,
                    fullEntity = entity
                )
            },
            selectedSimulation = selected,
            pendingUnfavorite = pending,
            isEditMode = isEdit
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesUiState()
    )

    fun toggleEditMode() {
        val newMode = !_isEditMode.value
        _isEditMode.value = newMode
        if (!newMode) {
            val current = _reorderedList.value
            if (current != null) {
                saveOrderToDb(current)
            }
        }
    }

    fun saveAndExitEditMode() {
        if (_isEditMode.value) {
            _isEditMode.value = false
            val current = _reorderedList.value
            if (current != null) {
                saveOrderToDb(current)
            }
        }
    }

    fun onMoveItem(fromIndex: Int, toIndex: Int) {
        val currentItems = uiState.value.favoriteSimulations.map { it.fullEntity }.toMutableList()
        if (fromIndex in currentItems.indices && toIndex in currentItems.indices && fromIndex != toIndex) {
            val moved = currentItems.removeAt(fromIndex)
            currentItems.add(toIndex, moved)
            _reorderedList.value = currentItems
            saveOrderToDb(currentItems)
        }
    }

    private fun saveOrderToDb(list: List<SimulationEntity>) {
        viewModelScope.launch {
            val updated = list.mapIndexed { index, entity ->
                entity.copy(favoriteOrder = index)
            }
            repository.updateSimulations(updated)
        }
    }

    fun onSimulationClick(simulation: SimulationEntity) {
        if (!_isEditMode.value) {
            _selectedSimulation.value = simulation
        }
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
            _reorderedList.value = null
        }
    }

    fun dismissUnfavoriteDialog() {
        _pendingUnfavorite.value = null
    }

    fun toggleAdsVisibility() {
        viewModelScope.launch {
            val current = adsPreferencesRepository.showAds.first()
            adsPreferencesRepository.setAdsVisible(!current)
        }
    }
}
