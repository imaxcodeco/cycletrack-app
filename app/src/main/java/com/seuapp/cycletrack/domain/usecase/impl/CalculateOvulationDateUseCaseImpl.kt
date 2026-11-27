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
        // Fase lútea é praticamente constante (~14 dias) independente do tamanho do ciclo;
        // quem varia é a fase folicular. Por isso ovulação = cycleLength - lutealPhase, não /2.
        val ovulationCycleDay = cycle.cycleLength - cycle.lutealPhaseLength
        return cycle.startDate.plusDays((ovulationCycleDay - 1).toLong())
    }
}
