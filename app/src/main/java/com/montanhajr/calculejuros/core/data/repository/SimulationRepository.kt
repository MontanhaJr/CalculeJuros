package com.montanhajr.calculejuros.core.data.repository

import com.montanhajr.calculejuros.core.data.db.SimulationDao
import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface SimulationRepository {
    fun getAllSimulations(): Flow<List<SimulationEntity>>
    fun getFavoriteSimulations(): Flow<List<SimulationEntity>>
    suspend fun getSimulationById(id: Long): SimulationEntity?
    suspend fun insertSimulation(simulation: SimulationEntity): Long
    suspend fun updateSimulations(simulations: List<SimulationEntity>)
    suspend fun deleteSimulation(simulation: SimulationEntity)
}

@Singleton
class SimulationRepositoryImpl @Inject constructor(
    private val simulationDao: SimulationDao
) : SimulationRepository {

    override fun getAllSimulations(): Flow<List<SimulationEntity>> {
        return simulationDao.getAllSimulations()
    }

    override fun getFavoriteSimulations(): Flow<List<SimulationEntity>> {
        return simulationDao.getFavoriteSimulations()
    }

    override suspend fun getSimulationById(id: Long): SimulationEntity? {
        return simulationDao.getSimulationById(id)
    }

    override suspend fun insertSimulation(simulation: SimulationEntity): Long {
        return simulationDao.insertSimulation(simulation)
    }

    override suspend fun updateSimulations(simulations: List<SimulationEntity>) {
        simulationDao.updateSimulations(simulations)
    }

    override suspend fun deleteSimulation(simulation: SimulationEntity) {
        simulationDao.deleteSimulation(simulation)
    }
}
