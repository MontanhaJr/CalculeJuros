package com.montanhajr.calculejuros.core.domain.usecase

import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.data.repository.SimulationRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: SimulationRepository
) {
    suspend operator fun invoke(simulation: SimulationEntity) {
        val updated = simulation.copy(isFavorite = !simulation.isFavorite)
        repository.insertSimulation(updated)
    }
}
