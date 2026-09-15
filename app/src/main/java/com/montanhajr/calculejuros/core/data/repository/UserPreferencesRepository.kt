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

val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

interface UserPreferencesRepository {
    val isPro: Flow<Boolean>
    suspend fun setProStatus(isPro: Boolean)
}

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {
    private object PreferencesKeys {
        val IS_PRO = booleanPreferencesKey("is_pro")
    }

    override val isPro: Flow<Boolean> = context.userPrefsDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_PRO] ?: false
        }

    override suspend fun setProStatus(isPro: Boolean) {
        context.userPrefsDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PRO] = isPro
        }
    }
}
