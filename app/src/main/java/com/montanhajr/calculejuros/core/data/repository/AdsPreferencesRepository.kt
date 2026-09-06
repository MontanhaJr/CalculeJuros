package com.montanhajr.calculejuros.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.adsDataStore: DataStore<Preferences> by preferencesDataStore(name = "ads_preferences")

interface AdsPreferencesRepository {
    val showAds: Flow<Boolean>
    suspend fun setAdsVisible(visible: Boolean)
}

@Singleton
class AdsPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AdsPreferencesRepository {
    private object PreferencesKeys {
        val SHOW_ADS = booleanPreferencesKey("show_ads")
    }

    override val showAds: Flow<Boolean> = context.adsDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SHOW_ADS] ?: true
        }

    override suspend fun setAdsVisible(visible: Boolean) {
        context.adsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ADS] = visible
        }
    }
}
