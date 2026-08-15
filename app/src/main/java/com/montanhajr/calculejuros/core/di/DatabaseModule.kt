package com.montanhajr.calculejuros.core.di

import android.content.Context
import androidx.room.Room
import com.montanhajr.calculejuros.core.data.db.AppDatabase
import com.montanhajr.calculejuros.core.data.db.SimulationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cashwise_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSimulationDao(database: AppDatabase): SimulationDao {
        return database.simulationDao()
    }
}
