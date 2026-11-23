package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.SymptomRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SymptomDao @Inject constructor() {

    private val realm: Realm
        get() = Realm.getDefaultInstance()

    suspend fun getSymptoms(userId: String): List<SymptomRealm> = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(SymptomRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .sort("date", io.realm.Sort.DESCENDING)
                .findAll()
                .let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSymptom(id: String): SymptomRealm? = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(SymptomRealm::class.java)
                .equalTo("id", id)
                .equalTo("deletedAt", 0L)
                .findFirst()
                ?.let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSymptomsForDate(userId: String, date: Long): List<SymptomRealm> = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(SymptomRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("date", date)
                .equalTo("deletedAt", 0L)
                .findAll()
                .let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSymptomsInRange(userId: String, startDate: Long, endDate: Long): List<SymptomRealm> =
        withContext(Dispatchers.IO) {
            try {
                val r = realm
                r.refresh()
                r.where(SymptomRealm::class.java)
                    .equalTo("userId", userId)
                    .between("date", startDate, endDate)
                    .equalTo("deletedAt", 0L)
                    .findAll()
                    .let { r.copyFromRealm(it) }
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun upsertSymptom(symptom: SymptomRealm) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction { it.insertOrUpdate(symptom) }
        } catch (e: Exception) {
            // Falha silenciosa: persistencia local e best-effort
        }
    }

    suspend fun upsertSymptoms(symptoms: List<SymptomRealm>) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction { it.insertOrUpdate(symptoms) }
        } catch (e: Exception) {
            // Falha silenciosa: persistencia local e best-effort
        }
    }

    suspend fun deleteSymptom(symptomId: String) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction { tx ->
                tx.where(SymptomRealm::class.java)
                    .equalTo("id", symptomId)
                    .findFirst()
                    ?.deleteFromRealm()
            }
        } catch (e: Exception) {
            // Falha silenciosa
        }
    }

    suspend fun getSymptomCount(userId: String): Int = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(SymptomRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .count()
                .toInt()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun hasSymptomOnDate(userId: String, date: Long, symptomName: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val r = realm
                r.refresh()
                r.where(SymptomRealm::class.java)
                    .equalTo("userId", userId)
                    .equalTo("date", date)
                    .equalTo("name", symptomName)
                    .equalTo("deletedAt", 0L)
                    .count() > 0
            } catch (e: Exception) {
                false
            }
        }
}
