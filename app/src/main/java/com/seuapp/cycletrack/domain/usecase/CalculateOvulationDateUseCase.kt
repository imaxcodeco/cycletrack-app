package com.seuapp.cycletrack.domain.usecase

import java.time.LocalDate

interface CalculateOvulationDateUseCase {
    suspend operator fun invoke(userId: String): LocalDate?
}
