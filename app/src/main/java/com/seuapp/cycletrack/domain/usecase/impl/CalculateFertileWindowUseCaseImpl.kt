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
        val ovulationCycleDay = cycle.cycleLength - cycle.lutealPhaseLength
        val ovulationDate = cycle.startDate.plusDays((ovulationCycleDay - 1).toLong())
        // Janela fértil = 6 dias terminando no dia da ovulação (ASRM).
        val start = ovulationDate.minusDays(5)
        val end = ovulationDate
        return FertileWindow(start, end)
    }
}
