package com.montanhajr.calculejuros.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.montanhajr.calculejuros.BuildConfig
import com.montanhajr.calculejuros.core.data.repository.AdsPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(
    adsPreferencesRepository: AdsPreferencesRepository
) : ViewModel() {
    val showAds: StateFlow<Boolean> = adsPreferencesRepository.showAds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
}

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    viewModel: AdViewModel = hiltViewModel()
) {
    val showAds by viewModel.showAds.collectAsState()

    AnimatedVisibility(
        visible = showAds,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = BuildConfig.ADMOB_BANNER_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
