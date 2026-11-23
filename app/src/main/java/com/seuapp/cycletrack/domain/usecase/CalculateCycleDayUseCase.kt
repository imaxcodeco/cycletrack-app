package com.seuapp.cycletrack.domain.usecase

interface CalculateCycleDayUseCase {
    suspend operator fun invoke(userId: String): Int
}
