package com.seuapp.cycletrack.domain.usecase

import java.time.LocalDate

data class LogSymptomRequest(
    val userId: String,
    val date: LocalDate,
    val name: String,
    val severity: Int
)

interface LogSymptomUseCase {
    suspend operator fun invoke(request: LogSymptomRequest)
}
