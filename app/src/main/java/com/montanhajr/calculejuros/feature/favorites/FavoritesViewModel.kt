package com.montanhajr.calculejuros.feature.favorites

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class FavoriteItem(
    val id: String,
    val title: String,
    val description: String,
    val creationDate: String,
    val resultType: String, // "Parcelar" or "À vista"
    val resultLabel: String, // "Você ganha" or "Você economiza"
    val resultValue: String,
    val iconType: String // "laptop", "phone", "tv", "watch", "ps5"
)

data class FavoritesUiState(
    val favoriteSimulations: List<FavoriteItem> = emptyList()
)

@HiltViewModel
class FavoritesViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(FavoritesUiState(
        favoriteSimulations = listOf(
            FavoriteItem(
                id = "1",
                title = "Notebook Dell i5",
                description = "12x no cartão • 10% à vista",
                creationDate = "Criado em 18/05/2024",
                resultType = "Parcelar",
                resultLabel = "Você ganha",
                resultValue = "R$ 184,32",
                iconType = "laptop"
            ),
            FavoriteItem(
                id = "2",
                title = "iPhone 15",
                description = "10x no cartão • 5% à vista",
                creationDate = "Criado em 15/05/2024",
                resultType = "À vista",
                resultLabel = "Você economiza",
                resultValue = "R$ 218,75",
                iconType = "phone"
            ),
            FavoriteItem(
                id = "3",
                title = "Smart TV 55\"",
                description = "8x no cartão • 8% à vista",
                creationDate = "Criado em 10/05/2024",
                resultType = "Parcelar",
                resultLabel = "Você ganha",
                resultValue = "R$ 96,80",
                iconType = "tv"
            ),
            FavoriteItem(
                id = "4",
                title = "Apple Watch Series 9",
                description = "6x no cartão • 0% à vista",
                creationDate = "Criado em 08/05/2024",
                resultType = "À vista",
                resultLabel = "Você economiza",
                resultValue = "R$ 75,40",
                iconType = "watch"
            ),
            FavoriteItem(
                id = "5",
                title = "PlayStation 5",
                description = "12x no cartão • 3% à vista",
                creationDate = "Criado em 05/05/2024",
                resultType = "Parcelar",
                resultLabel = "Você ganha",
                resultValue = "R$ 142,10",
                iconType = "ps5"
            )
        )
    ))
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
}
