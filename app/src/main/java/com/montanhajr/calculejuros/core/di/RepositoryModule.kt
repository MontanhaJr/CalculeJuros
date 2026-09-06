package com.montanhajr.calculejuros.core.di

import com.montanhajr.calculejuros.core.data.repository.AdsPreferencesRepository
import com.montanhajr.calculejuros.core.data.repository.AdsPreferencesRepositoryImpl
import com.montanhajr.calculejuros.core.data.repository.CurrencyPreferencesRepository
import com.montanhajr.calculejuros.core.data.repository.CurrencyPreferencesRepositoryImpl
import com.montanhajr.calculejuros.core.data.repository.SimulationRepository
import com.montanhajr.calculejuros.core.data.repository.SimulationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSimulationRepository(
        simulationRepositoryImpl: SimulationRepositoryImpl
    ): SimulationRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyPreferencesRepository(
        currencyPreferencesRepositoryImpl: CurrencyPreferencesRepositoryImpl
    ): CurrencyPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindAdsPreferencesRepository(
        adsPreferencesRepositoryImpl: AdsPreferencesRepositoryImpl
    ): AdsPreferencesRepository
}
