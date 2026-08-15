package com.montanhajr.calculejuros.core.domain.usecase

import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.data.repository.SimulationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: SimulationRepository
) {
    operator fun invoke(): Flow<List<SimulationEntity>> {
        return repository.getAllSimulations()
    }
}
