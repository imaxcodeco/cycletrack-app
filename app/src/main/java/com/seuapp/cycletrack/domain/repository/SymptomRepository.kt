package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Symptom
import kotlinx.coroutines.flow.Flow

interface SymptomRepository {
    fun getSymptoms(userId: String): Flow<List<Symptom>>
    suspend fun getSymptom(id: String): Symptom?
    suspend fun getSymptomsForDate(userId: String, date: Long): List<Symptom>
    suspend fun upsertSymptom(symptom: Symptom)
    suspend fun upsertSymptoms(symptoms: List<Symptom>)
    suspend fun deleteSymptom(id: String)
    suspend fun getSymptomCount(userId: String): Int
}
