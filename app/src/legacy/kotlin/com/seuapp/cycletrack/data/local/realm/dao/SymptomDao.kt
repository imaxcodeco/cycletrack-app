package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.SymptomRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * DAO for SymptomRealm operations.
 * Clean-room implementation with reactive queries.
 */
class SymptomDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get all non-deleted symptoms for a user, sorted by date descending.
     */
    suspend fun getSymptoms(userId: String): List<SymptomRealm> = withContext(Dispatchers.IO) {
        realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .sort("date", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get symptoms for a specific cycle.
     */
    suspend fun getSymptomsForCycle(cycleId: String): List<SymptomRealm> = withContext(Dispatchers.IO) {
        realm.where(SymptomRealm::class.java)
            .equalTo("cycleId", cycleId)
            .equalTo("deletedAt", 0L)
            .sort("date", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get symptoms for a specific date.
     */
    suspend fun getSymptomsForDate(userId: String, date: Long): List<SymptomRealm> = withContext(Dispatchers.IO) {
        realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("date", date)
            .sort("category")
            .findAll()
            .detached()
    }

    /**
     * Get symptoms in a date range.
     */
    suspend fun getSymptomsInRange(userId: String, startDate: Long, endDate: Long): List<SymptomRealm> = withContext(Dispatchers.IO) {
        realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .greaterThanOrEqualTo("date", startDate)
            .lessThanOrEqualTo("date", endDate)
            .sort("date", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get symptoms by category.
     */
    suspend fun getSymptomsByCategory(userId: String, category: String): List<SymptomRealm> = withContext(Dispatchers.IO) {
        realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("category", category)
            .sort("date", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get symptoms needing sync.
     */
    suspend fun getSymptomsNeedingSync(userId: String): List<SymptomRealm> = withContext(Dispatchers.IO) {
        realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .findAll()
            .filter { it.serverId.isEmpty() || it.updatedAt > it.lastSyncedAt }
            .detached()
    }

    /**
     * Insert or update a symptom.
     */
    suspend fun upsertSymptom(symptom: SymptomRealm): SymptomRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            symptom.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(symptom)
        }
    }

    /**
     * Insert multiple symptoms in a transaction.
     */
    suspend fun upsertSymptoms(symptoms: List<SymptomRealm>): List<SymptomRealm> = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            symptoms.forEach { it.updatedAt = System.currentTimeMillis() }
            realm.copyToRealmOrUpdate(symptoms)
        }
    }

    /**
     * Soft delete a symptom.
     */
    suspend fun deleteSymptom(symptomId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val symptom = realm.where(SymptomRealm::class.java)
                .equalTo("id", symptomId)
                .findFirst()
            symptom?.let {
                it.deletedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Hard delete a symptom (for GDPR/LGPD).
     */
    suspend fun hardDeleteSymptom(symptomId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val symptom = realm.where(SymptomRealm::class.java)
                .equalTo("id", symptomId)
                .findFirst()
            symptom?.deleteFromRealm() ?: false
        }
    }

    /**
     * Get symptom frequency for a user in a date range.
     */
    suspend fun getSymptomFrequency(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Map<String, Int> = withContext(Dispatchers.IO) {
        val symptoms = realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .greaterThanOrEqualTo("date", startDate)
            .lessThanOrEqualTo("date", endDate)
            .findAll()

        return symptoms
            .groupBy { it.name }
            .mapValues { (_, v) -> v.size }
    }

    /**
     * Get symptom severity distribution.
     */
    suspend fun getSymptomSeverityDistribution(
        userId: String,
        symptomName: String,
        startDate: Long,
        endDate: Long
    ): Map<Int, Int> = withContext(Dispatchers.IO) {
        val symptoms = realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("name", symptomName)
            .greaterThanOrEqualTo("date", startDate)
            .lessThanOrEqualTo("date", endDate)
            .findAll()

        return symptoms
            .groupBy { it.severity }
            .mapValues { (_, v) -> v.size }
    }

    /**
     * Get most common symptoms for a user.
     */
    suspend fun getMostCommonSymptoms(
        userId: String,
        limit: Int = 10,
        startDate: Long = 0L,
        endDate: Long = Long.MAX_VALUE
    ): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        val symptoms = if (startDate > 0 || endDate < Long.MAX_VALUE) {
            realm.where(SymptomRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .greaterThanOrEqualTo("date", startDate)
                .lessThanOrEqualTo("date", endDate)
                .findAll()
        } else {
            realm.where(SymptomRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .findAll()
        }

        return symptoms
            .groupBy { it.name }
            .mapValues { (_, v) -> v.size }
            .toList()
            .sortedByDescending { it.second }
            .take(limit)
    }

    /**
     * Get symptom count for a user.
     */
    suspend fun getSymptomCount(userId: String): Int = withContext(Dispatchers.IO) {
        realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .count()
            .toInt()
    }

    /**
     * Check if a symptom exists for a given date and name.
     */
    suspend fun hasSymptomOnDate(userId: String, date: Long, symptomName: String): Boolean = withContext(Dispatchers.IO) {
        realm.where(SymptomRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("date", date)
            .equalTo("name", symptomName)
            .findFirst() != null
    }

    /**
     * Executa uma transacao de escrita devolvendo o valor produzido pelo bloco.
     * A API Java do Realm nao retorna valor de `executeTransaction`.
     */
    private suspend fun <T> writeTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        var result: T? = null
        realm.executeTransaction { transactionRealm ->
            result = block(transactionRealm)
        }
        @Suppress("UNCHECKED_CAST")
        result as T
    }

}