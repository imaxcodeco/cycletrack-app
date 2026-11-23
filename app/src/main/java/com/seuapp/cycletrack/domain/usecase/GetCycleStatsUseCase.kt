package com.seuapp.cycletrack.domain.usecase

data class CycleStats(
    val averageCycleLength: Int,
    val averagePeriodLength: Int,
    val regularity: String
)

interface GetCycleStatsUseCase {
    suspend operator fun invoke(userId: String): CycleStats?
}
