package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.usecase.CalculateCycleDayUseCase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CalculateCycleDayUseCaseImpl @Inject constructor(
    private val cycleRepository: CycleRepository
) : CalculateCycleDayUseCase {
    override suspend fun invoke(userId: String): Int {
        val cycle = cycleRepository.getLatestCycle(userId) ?: return 1
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(cycle.startDate, today).toInt() + 1
    }
}
