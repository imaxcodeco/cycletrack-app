package com.seuapp.cycletrack.domain.usecase

import java.time.LocalDate

data class FertileWindow(val start: LocalDate, val end: LocalDate)

interface CalculateFertileWindowUseCase {
    suspend operator fun invoke(userId: String): FertileWindow?
}
