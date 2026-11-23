package com.seuapp.cycletrack.domain.usecase

interface GetSymptomHistoryUseCase {
    suspend operator fun invoke(userId: String, limit: Int = 30): List<com.seuapp.cycletrack.domain.model.Symptom>
}
