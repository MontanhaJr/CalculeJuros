package com.montanhajr.calculejuros.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.currencyDataStore: DataStore<Preferences> by preferencesDataStore(name = "currency_preferences")

interface CurrencyPreferencesRepository {
    val currencySymbol: Flow<String>
    suspend fun saveCurrencySymbol(symbol: String)
}

@Singleton
class CurrencyPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CurrencyPreferencesRepository {
    private object PreferencesKeys {
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
    }

    override val currencySymbol: Flow<String> = context.currencyDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CURRENCY_SYMBOL] ?: "R$"
        }

    override suspend fun saveCurrencySymbol(symbol: String) {
        context.currencyDataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY_SYMBOL] = symbol
        }
    }
}
