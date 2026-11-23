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

        return when {
            dayOfCycle <= cycle.periodLength -> CyclePhase.MENSTRUAL
            dayOfCycle <= cycle.cycleLength / 2 -> CyclePhase.FOLLICULAR
            dayOfCycle <= cycle.cycleLength / 2 + 2 -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }
    }
}
