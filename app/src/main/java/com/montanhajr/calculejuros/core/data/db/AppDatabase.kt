package com.montanhajr.calculejuros.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SimulationEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun simulationDao(): SimulationDao
}
