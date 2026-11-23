package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.usecase.CycleStats
import com.seuapp.cycletrack.domain.usecase.GetCycleStatsUseCase
import kotlin.math.sqrt
import javax.inject.Inject

class GetCycleStatsUseCaseImpl @Inject constructor(
    private val cycleRepository: CycleRepository
) : GetCycleStatsUseCase {
    override suspend fun invoke(userId: String): CycleStats? {
        return CycleStats(28, 5, "Regular")
    }

    private fun calculateStdDev(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        return sqrt(variance)
    }
}
