package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import com.seuapp.cycletrack.domain.usecase.LogSymptomRequest
import com.seuapp.cycletrack.domain.usecase.LogSymptomUseCase
import java.util.UUID
import javax.inject.Inject

class LogSymptomUseCaseImpl @Inject constructor(
    private val symptomRepository: SymptomRepository
) : LogSymptomUseCase {
    override suspend fun invoke(request: LogSymptomRequest) {
        val symptom = Symptom(
            id = UUID.randomUUID().toString(),
            userId = request.userId,
            date = request.date,
            name = request.name,
            severity = request.severity,
            notes = ""
        )
        symptomRepository.upsertSymptom(symptom)
    }
}
