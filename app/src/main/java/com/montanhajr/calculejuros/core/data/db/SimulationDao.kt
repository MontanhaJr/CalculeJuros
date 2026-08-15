package com.montanhajr.calculejuros.core.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SimulationDao {
    @Query("SELECT * FROM simulations ORDER BY date DESC")
    fun getAllSimulations(): Flow<List<SimulationEntity>>

    @Query("SELECT * FROM simulations WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavoriteSimulations(): Flow<List<SimulationEntity>>

    @Query("SELECT * FROM simulations WHERE id = :id")
    suspend fun getSimulationById(id: Long): SimulationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimulation(simulation: SimulationEntity)

    @Delete
    suspend fun deleteSimulation(simulation: SimulationEntity)
}
