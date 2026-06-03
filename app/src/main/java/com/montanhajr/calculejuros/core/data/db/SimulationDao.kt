package com.montanhajr.calculejuros.core.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SimulationDao {
    @Query("SELECT * FROM simulations ORDER BY date DESC")
    fun getAllSimulations(): Flow<List<SimulationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimulation(simulation: SimulationEntity)

    @Delete
    suspend fun deleteSimulation(simulation: SimulationEntity)
}
