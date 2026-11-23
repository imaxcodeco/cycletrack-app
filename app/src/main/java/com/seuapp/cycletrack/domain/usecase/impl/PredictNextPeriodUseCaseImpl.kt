package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.usecase.PredictNextPeriodUseCase
import java.time.LocalDate
import javax.inject.Inject

class PredictNextPeriodUseCaseImpl @Inject constructor(
    private val cycleRepository: CycleRepository
) : PredictNextPeriodUseCase {
    override suspend fun invoke(userId: String): LocalDate? {
        val cycle = cycleRepository.getLatestCycle(userId) ?: return null
        return cycle.startDate.plusDays(cycle.cycleLength.toLong())
    }
}
