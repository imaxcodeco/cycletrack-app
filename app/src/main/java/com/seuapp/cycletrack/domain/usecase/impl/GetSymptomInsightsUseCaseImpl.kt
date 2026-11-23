package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.repository.SymptomRepository
import com.seuapp.cycletrack.domain.usecase.GetSymptomInsightsUseCase
import com.seuapp.cycletrack.domain.usecase.SymptomInsight
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSymptomInsightsUseCaseImpl @Inject constructor(
    private val symptomRepository: SymptomRepository
) : GetSymptomInsightsUseCase {
    override suspend fun invoke(userId: String, limit: Int): List<SymptomInsight> {
        val symptoms = symptomRepository.getSymptoms(userId).first()
        return symptoms
            .groupingBy { it.name }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { SymptomInsight(it.key, it.value, "unknown") }
    }
}
