package com.montanhajr.calculejuros.feature.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "João da Silva",
    val userEmail: String = "joao.silva@email.com",
    val memberSince: String = "Membro desde abr/2024",
    val isPro: Boolean = true,
    val nextBillingDate: String = "12/06/2024",
    val totalSimulations: String = "24",
    val totalGain: String = "R$ 1.248,50",
    val totalFavorites: String = "12",
    val timeSaved: String = "38h"
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
