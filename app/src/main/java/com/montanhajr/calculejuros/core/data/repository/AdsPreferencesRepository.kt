package com.montanhajr.calculejuros.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.adsDataStore: DataStore<Preferences> by preferencesDataStore(name = "ads_preferences")

interface AdsPreferencesRepository {
    val showAds: Flow<Boolean>
    val interstitialCounter: Flow<Int>
    val skipNextRewardedAd: Flow<Boolean>
    suspend fun setAdsVisible(visible: Boolean)
    suspend fun incrementInterstitialCounter()
    suspend fun resetInterstitialCounter()
    suspend fun setSkipNextRewardedAd(skip: Boolean)
}

@Singleton
class AdsPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AdsPreferencesRepository {
    private object PreferencesKeys {
        val SHOW_ADS = booleanPreferencesKey("show_ads")
        val INTERSTITIAL_COUNTER = intPreferencesKey("interstitial_counter")
        val SKIP_NEXT_REWARDED = booleanPreferencesKey("skip_next_rewarded")
    }

    override val showAds: Flow<Boolean> = context.adsDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SHOW_ADS] ?: true
        }

    override val interstitialCounter: Flow<Int> = context.adsDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.INTERSTITIAL_COUNTER] ?: 0
        }

    override val skipNextRewardedAd: Flow<Boolean> = context.adsDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SKIP_NEXT_REWARDED] ?: false
        }

    override suspend fun setAdsVisible(visible: Boolean) {
        context.adsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ADS] = visible
        }
    }

    override suspend fun incrementInterstitialCounter() {
        context.adsDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.INTERSTITIAL_COUNTER] ?: 0
            preferences[PreferencesKeys.INTERSTITIAL_COUNTER] = current + 1
        }
    }

    override suspend fun resetInterstitialCounter() {
        context.adsDataStore.edit { preferences ->
            preferences[PreferencesKeys.INTERSTITIAL_COUNTER] = 0
        }
    }

    override suspend fun setSkipNextRewardedAd(skip: Boolean) {
        context.adsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SKIP_NEXT_REWARDED] = skip
        }
    }
}
