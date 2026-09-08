package com.montanhajr.calculejuros.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SimulationEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun simulationDao(): SimulationDao
}
