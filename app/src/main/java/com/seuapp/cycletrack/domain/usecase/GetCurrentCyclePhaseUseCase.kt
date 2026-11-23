package com.seuapp.cycletrack.domain.usecase

enum class CyclePhase {
    MENSTRUAL, FOLLICULAR, OVULATION, LUTEAL, UNKNOWN
}

interface GetCurrentCyclePhaseUseCase {
    suspend operator fun invoke(userId: String): CyclePhase
}
