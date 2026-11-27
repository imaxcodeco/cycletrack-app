package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.usecase.CyclePhase
import com.seuapp.cycletrack.domain.usecase.GetCurrentCyclePhaseUseCase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetCurrentCyclePhaseUseCaseImpl @Inject constructor(
    private val cycleRepository: CycleRepository
) : GetCurrentCyclePhaseUseCase {
    override suspend fun invoke(userId: String): CyclePhase {
        val cycle = cycleRepository.getLatestCycle(userId) ?: return CyclePhase.UNKNOWN
        val today = LocalDate.now()
        val dayOfCycle = ChronoUnit.DAYS.between(cycle.startDate, today).toInt() + 1

        // Fase lútea é praticamente constante (~14 dias) independente do tamanho do ciclo;
        // quem varia é a fase folicular. Por isso ovulação = cycleLength - lutealPhase, não /2.
        val ovulationCycleDay = cycle.cycleLength - cycle.lutealPhaseLength
        return when {
            dayOfCycle <= cycle.periodLength -> CyclePhase.MENSTRUAL
            dayOfCycle < ovulationCycleDay - 1 -> CyclePhase.FOLLICULAR
            dayOfCycle <= ovulationCycleDay -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }
    }
}
