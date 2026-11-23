package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.usecase.CalculateOvulationDateUseCase
import java.time.LocalDate
import javax.inject.Inject

class CalculateOvulationDateUseCaseImpl @Inject constructor(
    private val cycleRepository: CycleRepository
) : CalculateOvulationDateUseCase {
    override suspend fun invoke(userId: String): LocalDate? {
        val cycle = cycleRepository.getLatestCycle(userId) ?: return null
        return cycle.startDate.plusDays((cycle.cycleLength / 2).toLong())
    }
}
