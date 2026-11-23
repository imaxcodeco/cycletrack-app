package com.seuapp.cycletrack.domain.usecase

data class SymptomInsight(
    val symptom: String,
    val count: Int,
    val phase: String
)

interface GetSymptomInsightsUseCase {
    suspend operator fun invoke(userId: String, limit: Int = 5): List<SymptomInsight>
}
