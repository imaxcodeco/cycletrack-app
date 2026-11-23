package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import com.seuapp.cycletrack.domain.usecase.GetSymptomHistoryUseCase
import javax.inject.Inject

class GetSymptomHistoryUseCaseImpl @Inject constructor(
    private val symptomRepository: SymptomRepository
) : GetSymptomHistoryUseCase {
    override suspend fun invoke(userId: String, limit: Int): List<Symptom> {
        return emptyList()
    }
}
