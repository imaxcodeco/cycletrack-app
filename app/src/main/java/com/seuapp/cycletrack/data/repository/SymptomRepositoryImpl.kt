package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.SymptomDao
import com.seuapp.cycletrack.data.local.realm.entities.SymptomRealm
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SymptomRepositoryImpl @Inject constructor(
    private val symptomDao: SymptomDao
) : SymptomRepository {

    override fun getSymptoms(userId: String): Flow<List<Symptom>> = flow {
        emit(symptomDao.getSymptoms(userId).map { it.toDomain() })
    }

    override suspend fun getSymptom(id: String): Symptom? {
        return symptomDao.getSymptom(id)?.toDomain()
    }

    override suspend fun getSymptomsForDate(userId: String, date: Long): List<Symptom> {
        return symptomDao.getSymptomsForDate(userId, date).map { it.toDomain() }
    }

    override suspend fun upsertSymptom(symptom: Symptom) {
        symptomDao.upsertSymptom(symptom.toRealm())
    }

    override suspend fun upsertSymptoms(symptoms: List<Symptom>) {
        symptomDao.upsertSymptoms(symptoms.map { it.toRealm() })
    }

    override suspend fun deleteSymptom(id: String) {
        symptomDao.deleteSymptom(id)
    }

    override suspend fun getSymptomCount(userId: String): Int {
        return symptomDao.getSymptomCount(userId)
    }

    private fun Symptom.toRealm(): SymptomRealm = SymptomRealm().apply {
        this.id = this@toRealm.id
        this.userId = this@toRealm.userId
        this.date = this@toRealm.date.toEpochDay()
        this.category = this@toRealm.category
        this.name = this@toRealm.name
        this.severity = this@toRealm.severity
        this.notes = this@toRealm.notes
        this.value = this@toRealm.value
        this.createdAt = this@toRealm.createdAt
    }

    private fun SymptomRealm.toDomain(): Symptom = Symptom(
        id = this.id,
        userId = this.userId,
        date = LocalDate.ofEpochDay(this.date),
        name = this.name,
        category = this.category,
        severity = this.severity,
        notes = this.notes,
        value = this.value,
        createdAt = this.createdAt
    )
}
