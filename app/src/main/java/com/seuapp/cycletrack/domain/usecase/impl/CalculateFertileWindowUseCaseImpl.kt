package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.usecase.CalculateFertileWindowUseCase
import com.seuapp.cycletrack.domain.usecase.FertileWindow
import javax.inject.Inject

class CalculateFertileWindowUseCaseImpl @Inject constructor(
    private val cycleRepository: CycleRepository
) : CalculateFertileWindowUseCase {
    override suspend fun invoke(userId: String): FertileWindow? {
        val cycle = cycleRepository.getLatestCycle(userId) ?: return null
        val ovulationDay = cycle.cycleLength / 2
        val ovulationDate = cycle.startDate.plusDays(ovulationDay.toLong())
        val start = ovulationDate.minusDays(4)
        val end = ovulationDate.plusDays(1)
        return FertileWindow(start, end)
    }
}
