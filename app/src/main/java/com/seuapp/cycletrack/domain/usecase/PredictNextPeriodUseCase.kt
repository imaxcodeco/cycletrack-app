package com.seuapp.cycletrack.domain.usecase

import java.time.LocalDate

interface PredictNextPeriodUseCase {
    suspend operator fun invoke(userId: String): LocalDate?
}
